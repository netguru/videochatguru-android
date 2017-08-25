package co.netguru.simplewebrtc

import android.content.Context
import android.os.Handler
import android.os.Looper
import co.netguru.simplewebrtc.constraints.AudioMediaConstraints
import co.netguru.simplewebrtc.constraints.OfferAnswerConstraints
import co.netguru.simplewebrtc.constraints.PeerConnectionConstraints
import co.netguru.simplewebrtc.util.Logger
import co.netguru.simplewebrtc.util.WebRtcUtils
import org.webrtc.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class WebRtcClient(context: Context,
                   private val localVideoWidth: Int = 1280,
                   private val localVideoHeight: Int = 720,
                   private val localVideoFps: Int = 24,
                   hardwareAcceleration: Boolean = true) : RemoteVideoListener {

    companion object {
        private const val INITIALIZE_AUDIO = true
        private const val INITIALIZE_VIDEO = true

        /**
         * Enable additional loging - this might be helpfull while resolving problems.
         * By default logs are turned off.
         */
        fun enableLogs(logsEnabled: Boolean) {
            Logging.enableLogToDebugOutput(if (logsEnabled) Logging.Severity.LS_INFO else Logging.Severity.LS_NONE)
            Logger.loggingEnabled = logsEnabled
        }
    }

    private val counter = AtomicInteger(0)
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var remoteVideoStream: VideoTrack? = null

    private var videoSource: VideoSource? = null
    private var localVideoTrack: VideoTrack? = null

    private lateinit var peerConnectionFactory: PeerConnectionFactory

    private lateinit var audioSource: AudioSource
    private lateinit var localAudioTrack: AudioTrack

    private var remoteView: SurfaceViewRenderer? = null
    private var remoteVideoRenderer: VideoRenderer? = null
    private var localView: SurfaceViewRenderer? = null
    private var localVideoRenderer: VideoRenderer? = null

    private val eglBase = EglBase.create()

    private val audioConstraints by lazy {
        val audioConstraints = MediaConstraints()
        audioConstraints.mandatory.add(AudioMediaConstraints.DISABLE_AUDIO_PROCESSING.toKeyValuePair(true))
        audioConstraints
    }

    private val offerAnswerConstraints by lazy {
        val offerAnswerConstraints = MediaConstraints()
        offerAnswerConstraints.mandatory.add(OfferAnswerConstraints.OFFER_TO_RECEIVE_AUDIO.toKeyValuePair(true))
        offerAnswerConstraints.mandatory.add(OfferAnswerConstraints.OFFER_TO_RECEIVE_VIDEO.toKeyValuePair(true))
        offerAnswerConstraints
    }

    private val offerAnswerRestartConstraints by lazy {
        val offerAnswerConstraints = MediaConstraints()
        offerAnswerConstraints.mandatory.add(OfferAnswerConstraints.OFFER_TO_RECEIVE_AUDIO.toKeyValuePair(true))
        offerAnswerConstraints.mandatory.add(OfferAnswerConstraints.OFFER_TO_RECEIVE_VIDEO.toKeyValuePair(true))
        offerAnswerConstraints.mandatory.add(OfferAnswerConstraints.ICE_RESTART.toKeyValuePair(true))
        offerAnswerConstraints
    }

    private val peerConnectionConstraints by lazy {
        val peerConnectionConstraints = MediaConstraints()
        peerConnectionConstraints.mandatory.add(PeerConnectionConstraints.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT.toKeyValuePair(true))
        peerConnectionConstraints.mandatory.add(PeerConnectionConstraints.GOOG_CPU_OVERUSE_DETECTION.toKeyValuePair(true))
        peerConnectionConstraints
    }

    private val videoCameraCapturer = WebRtcUtils.createCameraCapturerWithFrontAsDefault(context)

    var cameraEnabled = true
        set(isEnabled) {
            field = isEnabled
            singleThreadExecutor.execute {
                videoCameraCapturer?.let { enableVideo(isEnabled, it) }
            }
        }
    var microphoneEnabled = true
        set(isEnabled) {
            field = isEnabled
            singleThreadExecutor.execute {
                localAudioTrack.setEnabled(isEnabled)
            }
        }

    private var isPeerConnectionInitialized = false

    private lateinit var peerConnectionListener: PeerConnectionListener

    private val videoPeerConnectionListener by lazy { VideoPeerConnectionObserver(peerConnectionListener, this) }

    private lateinit var peerConnection: PeerConnection

    private lateinit var offeringPartyHandler: WebRtcOfferingPartyHandler
    private lateinit var answeringPartyHandler: WebRtcAnsweringPartyHandler

    init {
        if (!PeerConnectionFactory.initializeAndroidGlobals(context.applicationContext, INITIALIZE_AUDIO, INITIALIZE_VIDEO, hardwareAcceleration)) {
            error("WebRtc failed to initializeAndroidGlobals")
        }
        singleThreadExecutor.execute {
            initialize()
        }
    }

    private fun initialize() {
        peerConnectionFactory = PeerConnectionFactory(PeerConnectionFactory.Options())

        if (videoCameraCapturer != null) {
            peerConnectionFactory.setVideoHwAccelerationOptions(eglBase.eglBaseContext, eglBase.eglBaseContext)
            videoSource = peerConnectionFactory.createVideoSource(videoCameraCapturer)
            localVideoTrack = peerConnectionFactory.createVideoTrack(counter.getAndIncrement().toString(), videoSource)
            enableVideo(cameraEnabled, videoCameraCapturer)
        }

        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack(getCounterStringValueAndIncrement(), audioSource)
    }

    fun initializePeerConnection(iceServers: List<PeerConnection.IceServer>,
                                 peerConnectionListener: PeerConnectionListener,
                                 webRtcOfferingActionListener: WebRtcOfferingActionListener,
                                 webRtcAnsweringPartyListener: WebRtcAnsweringPartyListener) {
        isPeerConnectionInitialized = true
        singleThreadExecutor.execute {
            this.peerConnectionListener = peerConnectionListener
            val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
            peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, peerConnectionConstraints, videoPeerConnectionListener)

            val stream = peerConnectionFactory.createLocalMediaStream(getCounterStringValueAndIncrement())

            stream.addTrack(localAudioTrack)
            localVideoTrack?.let { stream.addTrack(it) }

            peerConnection.addStream(stream)
            offeringPartyHandler = WebRtcOfferingPartyHandler(peerConnection, webRtcOfferingActionListener)
            answeringPartyHandler = WebRtcAnsweringPartyHandler(peerConnection, offerAnswerConstraints, webRtcAnsweringPartyListener)
        }
    }

    override fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack) {
        singleThreadExecutor.execute {
            remoteVideoStream = remoteVideoTrack
            remoteVideoRenderer?.let {
                remoteVideoTrack.addRenderer(it)
            }
        }
    }

    override fun removeVideoStream() {
        singleThreadExecutor.execute {
            remoteVideoStream = null
        }
    }

    fun attachRemoteView(remoteView: SurfaceViewRenderer) {
        mainThreadHandler.run {
            remoteView.init(eglBase.eglBaseContext, null)
            this@WebRtcClient.remoteView = remoteView
            singleThreadExecutor.execute {
                remoteVideoRenderer = VideoRenderer(remoteView)
                remoteVideoStream?.addRenderer(remoteVideoRenderer)
            }
        }
    }

    fun attachLocalView(localView: SurfaceViewRenderer) {
        mainThreadHandler.run {
            localView.init(eglBase.eglBaseContext, null)
            this@WebRtcClient.localView = localView
            singleThreadExecutor.execute {
                localVideoRenderer = VideoRenderer(localView)
                localVideoTrack?.addRenderer(localVideoRenderer)
            }
        }

    }

    fun detachViews() {
        mainThreadHandler.run {
            remoteView?.release()
            remoteView = null
            localView?.release()
            localView = null
            singleThreadExecutor.execute {
                remoteVideoRenderer?.let {
                    remoteVideoStream?.removeRenderer(it)
                    remoteVideoRenderer = null
                }
                localVideoRenderer?.let {
                    localVideoTrack?.removeRenderer(it)
                    localVideoRenderer = null
                }
            }
        }

    }

    fun dispose() {
        singleThreadExecutor.execute {
            eglBase.release()
            audioSource.dispose()
            videoCameraCapturer?.dispose()
            videoSource?.dispose()
            peerConnectionFactory.dispose()
        }
        singleThreadExecutor.shutdown()
    }

    /**
     * If peer connection was initialized make sure that this method is called before [dispose]
     */
    fun releasePeerConnection() {
        if (isPeerConnectionInitialized) {
            singleThreadExecutor.execute {
                peerConnection.close()
                peerConnection.dispose()
            }
        }
    }

    fun createOffer() {
        singleThreadExecutor.execute {
            offeringPartyHandler.createOffer(offerAnswerConstraints)
        }
    }

    fun handleRemoteAnswer(remoteSessionDescription: SessionDescription) {
        singleThreadExecutor.execute {
            offeringPartyHandler.handleRemoteAnswer(remoteSessionDescription)
        }
    }

    fun handleRemoteOffer(remoteSessionDescription: SessionDescription) {
        singleThreadExecutor.execute {
            answeringPartyHandler.handleRemoteOffer(remoteSessionDescription)
        }
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        singleThreadExecutor.execute {
            peerConnection.addIceCandidate(iceCandidate)
        }
    }

    fun removeIceCandidate(iceCandidates: Array<IceCandidate>) {
        singleThreadExecutor.execute {
            peerConnection.removeIceCandidates(iceCandidates)
        }
    }

    fun restart() {
        singleThreadExecutor.execute {
            offeringPartyHandler.createOffer(offerAnswerRestartConstraints)
        }
    }

    fun switchCamera() {
        singleThreadExecutor.execute {
            videoCameraCapturer?.switchCamera(null)
        }
    }

    private fun enableVideo(isEnabled: Boolean, videoCapturer: CameraVideoCapturer) {
        if (isEnabled)
            videoCapturer.startCapture(localVideoWidth, localVideoHeight, localVideoFps)
        else
            videoCapturer.stopCapture()
    }

    private fun getCounterStringValueAndIncrement() = counter.getAndIncrement().toString()
}
package co.netguru.videochatguru

import android.content.Context
import android.os.Handler
import android.os.Looper
import co.netguru.videochatguru.constraints.*
import co.netguru.videochatguru.util.Logger
import co.netguru.videochatguru.util.WebRtcUtils
import co.netguru.videochatguru.util.addConstraints
import org.webrtc.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * WebRTC client wraps webRTC implementation simplifying implementation of video chat. WebRTC client
 * uses set of default WebRTC constraints that should suffice most of the use cases if you need to overwrite
 * those you can pass your own [WebRtcConstraints] collection of constraints.
 *
 * @param context used only during camera initialization, not stored internally
 * @param localVideoWidth width of video recorded by this client
 * @param localVideoHeight height of video recorded by this client
 * @param localVideoFps frames per second recorded by this client
 * @param hardwareAcceleration set whether client should use hardware acceleration, enabled by default
 * @param booleanAudioConstraints enables overwriting default [BooleanAudioConstraints] used by client
 * @param integerAudioConstraints enables overwriting default [IntegerAudioConstraints] used by client
 * @param peerConnectionConstraints enables overwriting default [PeerConnectionConstraints] used by client
 * @param offerAnswerConstraints enables overwriting default [OfferAnswerConstraints] used by client
 */
open class WebRtcClient(context: Context,
                        private val localVideoWidth: Int = 1280,
                        private val localVideoHeight: Int = 720,
                        private val localVideoFps: Int = 24,
                        hardwareAcceleration: Boolean = true,
                        booleanAudioConstraints: WebRtcConstraints<BooleanAudioConstraints, Boolean>? = null,
                        integerAudioConstraints: WebRtcConstraints<IntegerAudioConstraints, Int>? = null,
                        peerConnectionConstraints: WebRtcConstraints<PeerConnectionConstraints, Boolean>? = null,
                        offerAnswerConstraints: WebRtcConstraints<OfferAnswerConstraints, Boolean>? = null) : RemoteVideoListener {

    companion object {
        private val TAG = WebRtcClient::class.java.simpleName
        //Enabling internal tracer was causing crashes
        private const val ENABLE_INTERNAL_TRACER = false
    }

    private val counter = AtomicInteger(0)
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var remoteVideoTrack: VideoTrack? = null

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

    private val audioBooleanConstraints by lazy {
        WebRtcConstraints<BooleanAudioConstraints, Boolean>().apply {
            addMandatoryConstraint(BooleanAudioConstraints.DISABLE_AUDIO_PROCESSING, true)
        }
    }

    private val audioIntegerConstraints by lazy {
        WebRtcConstraints<IntegerAudioConstraints, Int>()
    }

    private val offerAnswerConstraints by lazy {
        WebRtcConstraints<OfferAnswerConstraints, Boolean>().apply {
            addMandatoryConstraint(OfferAnswerConstraints.OFFER_TO_RECEIVE_AUDIO, true)
            addMandatoryConstraint(OfferAnswerConstraints.OFFER_TO_RECEIVE_VIDEO, true)
        }
    }

    private val peerConnectionConstraints by lazy {
        WebRtcConstraints<PeerConnectionConstraints, Boolean>().apply {
            addMandatoryConstraint(PeerConnectionConstraints.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, true)
            addMandatoryConstraint(PeerConnectionConstraints.GOOG_CPU_OVERUSE_DETECTION, true)
        }
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
    private lateinit var peerConnection: PeerConnection

    private lateinit var peerConnectionListener: PeerConnectionListener

    private val videoPeerConnectionListener by lazy { VideoPeerConnectionObserver(peerConnectionListener, this) }

    private lateinit var offeringPartyHandler: WebRtcOfferingPartyHandler
    private lateinit var answeringPartyHandler: WebRtcAnsweringPartyHandler

    init {
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(ENABLE_INTERNAL_TRACER)
                        .setEnableVideoHwAcceleration(hardwareAcceleration)
                        .createInitializationOptions()
        )
        booleanAudioConstraints?.let {
            audioBooleanConstraints += it
        }
        integerAudioConstraints?.let {
            audioIntegerConstraints += it
        }
        peerConnectionConstraints?.let {
            this.peerConnectionConstraints += it
        }
        offerAnswerConstraints?.let {
            this.offerAnswerConstraints += it
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

        audioSource = peerConnectionFactory.createAudioSource(getAudioMediaConstraints())
        localAudioTrack = peerConnectionFactory.createAudioTrack(getCounterStringValueAndIncrement(), audioSource)
    }

    /**
     * Initialize the peer connection
     * @param iceServers list of interactive connectivity establishment servers used for traversal or relaying media (Stun and Turn)
     * @param peerConnectionListener listener for interactive connectivity establishment actions
     * @param webRtcOfferingActionListener offering party actions listener
     * @param webRtcAnsweringPartyListener answering party actions listener
     */
    fun initializePeerConnection(iceServers: List<PeerConnection.IceServer>,
                                 peerConnectionListener: PeerConnectionListener,
                                 webRtcOfferingActionListener: WebRtcOfferingActionListener,
                                 webRtcAnsweringPartyListener: WebRtcAnsweringPartyListener) {
        singleThreadExecutor.execute {
            this.peerConnectionListener = peerConnectionListener
            val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
            peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, getPeerConnectionMediaConstraints(), videoPeerConnectionListener)
            isPeerConnectionInitialized = true

            val localMediaStream = peerConnectionFactory.createLocalMediaStream(getCounterStringValueAndIncrement())

            localMediaStream.addTrack(localAudioTrack)
            localVideoTrack?.let { localMediaStream.addTrack(it) }

            peerConnection.addStream(localMediaStream)
            offeringPartyHandler = WebRtcOfferingPartyHandler(peerConnection, webRtcOfferingActionListener)
            answeringPartyHandler = WebRtcAnsweringPartyHandler(peerConnection, getOfferAnswerConstraints(), webRtcAnsweringPartyListener)
        }
    }

    override fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack) {
        singleThreadExecutor.execute {
            this.remoteVideoTrack = remoteVideoTrack
            remoteVideoRenderer?.let {
                remoteVideoTrack.addRenderer(it)
            }
        }
    }

    override fun removeVideoStream() {
        singleThreadExecutor.execute {
            remoteVideoTrack = null
        }
    }

    /**
     * Attach [SurfaceViewRenderer] to webrtc client used for rendering remote view.
     */
    fun attachRemoteView(remoteView: SurfaceViewRenderer) {
        mainThreadHandler.post {
            remoteView.init(eglBase.eglBaseContext, null)
            this@WebRtcClient.remoteView = remoteView
            singleThreadExecutor.execute {
                remoteVideoRenderer = VideoRenderer(remoteView)
                remoteVideoTrack?.addRenderer(remoteVideoRenderer)
            }
        }
    }

    /**
     * Attach [SurfaceViewRenderer] to webrtc client used for rendering local view.
     */
    fun attachLocalView(localView: SurfaceViewRenderer) {
        mainThreadHandler.post {
            localView.init(eglBase.eglBaseContext, null)
            this@WebRtcClient.localView = localView
            singleThreadExecutor.execute {
                localVideoRenderer = VideoRenderer(localView)
                localVideoTrack?.addRenderer(localVideoRenderer)
            }
        }

    }

    fun detachLocalView() {
        mainThreadHandler.post {
            localView?.release()
            localView = null
        }
        singleThreadExecutor.execute {
            localVideoRenderer?.let {
                localVideoTrack?.removeRenderer(it)
                it.dispose()
                localVideoRenderer = null
            }
        }
    }

    fun detachRemoteView() {
        mainThreadHandler.post {
            remoteView?.release()
            remoteView = null
        }
        singleThreadExecutor.execute {
            remoteVideoRenderer?.let {
                remoteVideoTrack?.removeRenderer(it)
                it.dispose()
                remoteVideoRenderer = null
            }
        }
    }

    /**
     * Detach all [SurfaceViewRenderer]'s from webrtc client.
     */
    fun detachViews() {
        detachLocalView()
        detachRemoteView()
    }

    /**
     * Call this method after you finish using this WebRtcClient instance.
     */
    fun dispose() {
        singleThreadExecutor.execute {
            if (isPeerConnectionInitialized) {
                peerConnection.close()
                peerConnection.dispose()
            }
            eglBase.release()
            audioSource.dispose()
            videoCameraCapturer?.dispose()
            videoSource?.dispose()
            peerConnectionFactory.dispose()
        }
        singleThreadExecutor.shutdown()
    }

    /**
     * Orders webrtc client to create offer for remote party. Offer will be returned in [WebRtcOfferingActionListener] callback
     */
    fun createOffer() {
        singleThreadExecutor.execute {
            offeringPartyHandler.createOffer(getOfferAnswerConstraints())
        }
    }

    /**
     * Handles received remote answer to our offer.
     */
    fun handleRemoteAnswer(remoteSessionDescription: SessionDescription) {
        singleThreadExecutor.execute {
            offeringPartyHandler.handleRemoteAnswer(remoteSessionDescription)
        }
    }

    /**
     * Handles received remote offer. This will result in producing answer which will be returned in
     * [WebRtcAnsweringPartyListener] callback.
     */
    fun handleRemoteOffer(remoteSessionDescription: SessionDescription) {
        singleThreadExecutor.execute {
            answeringPartyHandler.handleRemoteOffer(remoteSessionDescription)
        }
    }

    /**
     * Adds ice candidate from remote party to webrtc client
     */
    fun addRemoteIceCandidate(iceCandidate: IceCandidate) {
        singleThreadExecutor.execute {
            peerConnection.addIceCandidate(iceCandidate)
        }
    }

    /**
     * Removes ice candidates
     */
    fun removeRemoteIceCandidate(iceCandidates: Array<IceCandidate>) {
        singleThreadExecutor.execute {
            peerConnection.removeIceCandidates(iceCandidates)
        }
    }

    /**
     * Tries to start connection again, this should be called when connection state changes to
     * [PeerConnection.IceConnectionState.DISCONNECTED] or [PeerConnection.IceConnectionState.FAILED]
     * by one of the parties - preferably offering one.
     */
    fun restart() {
        singleThreadExecutor.execute {
            offeringPartyHandler.createOffer(getOfferAnswerRestartConstraints())
        }
    }

    /**
     * Switches the camera to other if there is any available. By default front camera is used.
     * @param cameraSwitchHandler allows listening for switch camera event
     */
    @JvmOverloads
    fun switchCamera(cameraSwitchHandler: CameraVideoCapturer.CameraSwitchHandler? = null) {
        singleThreadExecutor.execute {
            videoCameraCapturer?.switchCamera(cameraSwitchHandler)
        }
    }

    /**
     * Safety net in case the owner of an object forgets to call its explicit termination method.
     * @see <a href="https://kotlinlang.org/docs/reference/java-interop.html#finalize">
     *     https://kotlinlang.org/docs/reference/java-interop.html#finalize</a>
     */
    @Suppress("unused", "ProtectedInFinal")
    protected fun finalize() {
        if (!singleThreadExecutor.isShutdown) {
            Logger.e(TAG, "Dispose method wasn't called")
            dispose()
        }

    }

    private fun enableVideo(isEnabled: Boolean, videoCapturer: CameraVideoCapturer) {
        if (isEnabled) {
            videoCapturer.startCapture(localVideoWidth, localVideoHeight, localVideoFps)
        } else {
            videoCapturer.stopCapture()
        }
    }

    private fun getCounterStringValueAndIncrement() = counter.getAndIncrement().toString()

    private fun getAudioMediaConstraints() = MediaConstraints().apply {
        addConstraints(audioBooleanConstraints, audioIntegerConstraints)
    }

    private fun getPeerConnectionMediaConstraints() = MediaConstraints().apply {
        addConstraints(peerConnectionConstraints)
    }

    private fun getOfferAnswerConstraints() = MediaConstraints().apply {
        addConstraints(offerAnswerConstraints)
    }

    private fun getOfferAnswerRestartConstraints() = getOfferAnswerConstraints().apply {
        mandatory.add(OfferAnswerConstraints.ICE_RESTART.toKeyValuePair(true))
    }
}
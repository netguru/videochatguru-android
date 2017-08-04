package co.netguru.chatroulette.feature.main.video

import android.content.Context
import org.webrtc.*
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils
import timber.log.Timber

class WebRtcClient(context: Context) : RemoteVideoListener {

    companion object {
        private const val INITIALIZE_AUDIO = true
        private const val INITIALIZE_VIDEO = true
        private const val HW_ACCELERATION = true
    }

    private var remoteVideoStream: VideoTrack? = null
    private var localVideoTrack: VideoTrack? = null

    private var remoteVideoRenderer: VideoRenderer? = null
    private var localVideoRenderer: VideoRenderer? = null

    private val eglBase = EglBase.create()
    private val peerConnectionFactory by lazy { PeerConnectionFactory(PeerConnectionFactory.Options()) }

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

    private val peerConnectionConstraints by lazy {
        val peerConnectionConstraitns = MediaConstraints()
        peerConnectionConstraitns.mandatory.add(PeerConnectionConstraints.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT.toKeyValuePair(true))
        peerConnectionConstraitns.mandatory.add(PeerConnectionConstraints.GOOG_CPU_OVERUSE_DETECTION.toKeyValuePair(true))
        peerConnectionConstraitns
    }

    private val frontCameraCapturer = WebRtcUtils.createFrontCameraCapturer(context)
    private val backCameraCapturer = WebRtcUtils.createBackCameraCapturer(context)

    var isFrontCameraUsed = true

    private var localAudioTrack: AudioTrack

    private lateinit var peerConnectionListener: PeerConnectionListener

    private val videoPeerConnectionListener by lazy { VideoPeerConnectionObserver(peerConnectionListener, this) }

    private lateinit var peer: PeerConnection

    init {
        if (!PeerConnectionFactory.initializeAndroidGlobals(context.applicationContext, INITIALIZE_AUDIO, INITIALIZE_VIDEO, HW_ACCELERATION)) {
            Timber.d("Failed to initializeAndroidGlobals")
        }

        //todo check if needed
        WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true /* enable */)
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false)
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false)

        if (isCameraAvailable()) {
            val videoCapturer = getCurrentVideoCapturer()
            peerConnectionFactory.setVideoHwAccelerationOptions(eglBase.eglBaseContext, eglBase.eglBaseContext)
            val source = peerConnectionFactory.createVideoSource(videoCapturer)
            localVideoTrack = peerConnectionFactory.createVideoTrack("100", source)
            videoCapturer?.startCapture(1280, 720, 30)
            //todo check if really needed
        }

        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
    }

    fun initialize(iceServers: List<PeerConnection.IceServer>, peerConnectionListener: PeerConnectionListener) {
        this.peerConnectionListener = peerConnectionListener
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        peer = peerConnectionFactory.createPeerConnection(rtcConfig, peerConnectionConstraints, videoPeerConnectionListener)

        val stream = peerConnectionFactory.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        peer.addStream(stream)
    }

    override fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack) {
        remoteVideoStream = remoteVideoTrack
        remoteVideoRenderer?.let { remoteVideoTrack.addRenderer(it) }
    }

    override fun removeVideoStream() {
        remoteVideoStream = null
    }

    private fun getCurrentVideoCapturer() = if (isFrontCameraUsed && frontCameraCapturer != null) frontCameraCapturer else backCameraCapturer

    fun attachRemoteView(remoteView: SurfaceViewRenderer, renderListener: RendererCommon.RendererEvents? = null) {
        //TODO("implement")
        remoteView.init(eglBase.eglBaseContext, renderListener)
        remoteView.setEnableHardwareScaler(true)
        remoteVideoRenderer = VideoRenderer(remoteView)
        remoteVideoStream?.addRenderer(remoteVideoRenderer)
    }

    fun detachRemoteView() {
        remoteVideoStream?.removeRenderer(remoteVideoRenderer)
    }

    fun attachLocalView(localView: SurfaceViewRenderer, renderListener: RendererCommon.RendererEvents? = null) {
        //TODO() think how to handle old views
        localView.init(eglBase.eglBaseContext, renderListener)
        localView.setEnableHardwareScaler(true)
        localVideoRenderer = VideoRenderer(localView)
        localVideoTrack?.addRenderer(localVideoRenderer)
    }

    fun detachLocalView() {
        localVideoTrack?.removeRenderer(localVideoRenderer)
    }

    fun dispose() {
        eglBase.release()
    }

    //offering party
    fun createOffer() {
        peer.createOffer(object : SdpCreateObserver {
            override fun onCreateSuccess(localSessionDescription: SessionDescription) {
                setLocalDescription(localSessionDescription)
            }

            override fun onCreateFailure(error: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, offerAnswerConstraints)
    }

    private fun setLocalDescription(localSessionDescription: SessionDescription) {
        peer.setLocalDescription(object : SdpSetObserver {

            override fun onSetSuccess() {
                peerConnectionListener.onOffer(localSessionDescription)
            }

            override fun onSetFailure(error: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, localSessionDescription)
    }

    fun handleRemoteAnswer(remoteSessionDescription: SessionDescription) {
        peer.setRemoteDescription(object : SdpSetObserver {
            override fun onSetSuccess() {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Timber.d("setRemoteDescription from answer success")
            }

            override fun onSetFailure(error: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, remoteSessionDescription)
    }

    //answering party
    fun handleRemoteOffer(remoteSessionDescription: SessionDescription) {
        peer.setRemoteDescription(object : SdpSetObserver {
            override fun onSetSuccess() {
                createAnswer()
            }

            override fun onSetFailure(error: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, remoteSessionDescription)
    }

    private fun createAnswer() {
        peer.createAnswer(object : SdpCreateObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                setLocalADescription(sessionDescription)
            }

            override fun onCreateFailure(error: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, offerAnswerConstraints)
    }

    fun setLocalADescription(sessionDescription: SessionDescription) {
        peer.setLocalDescription(object : SdpSetObserver {
            override fun onSetFailure(p0: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSetSuccess() {
                peerConnectionListener.onAnswer(sessionDescription)
            }

        }, sessionDescription)
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        peer.addIceCandidate(iceCandidate)
    }

    fun removeIceCandidate(iceCandidate: IceCandidate) {
        peer.removeIceCandidates(arrayOf(iceCandidate))
    }


    private fun isCameraAvailable() = (frontCameraCapturer != null || backCameraCapturer != null)
}
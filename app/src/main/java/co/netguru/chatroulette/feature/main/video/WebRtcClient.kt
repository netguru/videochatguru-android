package co.netguru.chatroulette.feature.main.video

import android.content.Context
import org.webrtc.*
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils
import timber.log.Timber

class WebRtcClient(context: Context) {

    companion object {
        private const val INITIALIZE_AUDIO = true
        private const val INITIALIZE_VIDEO = true
        private const val HW_ACCELERATION = true

    }

    private val eglBase by lazy { EglBase.create() }
    private val peerConnectionFactory by lazy { PeerConnectionFactory(PeerConnectionFactory.Options()) }

    private val mediaConstraints by lazy {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.optional.add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))
    }
    private val sdpConstraints by lazy {
        val sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(OfferConstraints.OFFER_TO_RECEIVE_AUDIO.toKeyValuePair(true))
        sdpConstraints.mandatory.add(OfferConstraints.OFFER_TO_RECEIVE_VIDEO.toKeyValuePair(true))
    }

    private val frontCameraCapturer = WebRtcUtils.createFrontCameraCapturer(context)
    private val backCameraCapturer = WebRtcUtils.createBackCameraCapturer(context)

    var isFrontCameraUsed = true

    private lateinit var localVideoTrack: VideoTrack

    init {
        if (!PeerConnectionFactory.initializeAndroidGlobals(context.applicationContext, INITIALIZE_AUDIO, INITIALIZE_VIDEO, HW_ACCELERATION)) {
            Timber.d("Failed to initializeAndroidGlobals")
        }
        //not nee
        //PeerConnectionFactory.initializeInternalTracer()
        //not needed
        //PeerConnectionFactory.initializeFieldTrials("")

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
            localVideoTrack.setEnabled(true)
        }

    }

    private fun getCurrentVideoCapturer() = if (isFrontCameraUsed && frontCameraCapturer != null) frontCameraCapturer else backCameraCapturer

    fun attachRemoteView(remoteView: SurfaceViewRenderer, renderListener: RendererCommon.RendererEvents? = null) {
        //TODO("implement")
        remoteView.init(eglBase.eglBaseContext, renderListener)
        remoteView.setEnableHardwareScaler(true)
    }

    fun detachRemoteView() {
        TODO()
    }

    fun attachLocalView(localView: SurfaceViewRenderer, renderListener: RendererCommon.RendererEvents? = null) {
        //TODO() think how to handle old views
        localView.init(eglBase.eglBaseContext, renderListener)
        localView.setEnableHardwareScaler(true)
        localVideoTrack.addRenderer(VideoRenderer(localView))
    }

    fun detachLocalView() {
        TODO()
    }

    private fun isCameraAvailable() = (frontCameraCapturer != null || backCameraCapturer != null)
}
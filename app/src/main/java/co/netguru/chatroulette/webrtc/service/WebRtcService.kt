package co.netguru.chatroulette.webrtc.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.feature.base.service.BaseServiceWithFacade
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber
import javax.inject.Inject


class WebRtcService : BaseServiceWithFacade<WebRtcServiceFacade, WebRtcServiceController>(), WebRtcServiceFacade {

    @Inject lateinit var webRtcServiceController: WebRtcServiceController

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        App.getApplicationComponent(this).webRtcServiceComponent().inject(this)
        Timber.d("WebRtc service created")
        super.onCreate()
    }

    override fun retrieveController(): WebRtcServiceController = webRtcServiceController

    override fun stop() {
        stopSelf()
    }

    fun attachServiceActionsListener(webRtcServiceListener: WebRtcServiceListener) {
        webRtcServiceController.serviceListener = webRtcServiceListener
    }

    fun detachServiceActionsListener() {
        webRtcServiceController.serviceListener = null
    }

    fun offerDevice(deviceUuid: String) {
        webRtcServiceController.offerDevice(deviceUuid)
    }

    fun attachRemoteView(remoteView: SurfaceViewRenderer) {
        webRtcServiceController.attachRemoteView(remoteView)
    }

    fun attachLocalView(localView: SurfaceViewRenderer) {
        webRtcServiceController.attachLocalView(localView)
    }

    fun detachViews() {
        webRtcServiceController.detachViews()
    }

    fun getRemoteUuid() = webRtcServiceController.remoteUuid

    fun switchCamera() = webRtcServiceController.switchCamera()

    fun enableCamera(isEnabled: Boolean) {
        webRtcServiceController.enableCamera(isEnabled)
    }

    fun isCameraEnabled() = webRtcServiceController.isCameraEnabled()

    fun enableMicrophone(isEnabled: Boolean) {
        webRtcServiceController.enableMicrophone(isEnabled)
    }

    fun isMicrophoneEnabled() = webRtcServiceController.isMicrophoneEnabled()

    inner class LocalBinder : Binder() {
        val service: WebRtcService
            get() = this@WebRtcService
    }
}


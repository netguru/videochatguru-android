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

    @Inject lateinit var webRtcServiceManager: WebRtcServiceController

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        App.getApplicationComponent(this).webRtcServiceComponent().inject(this)
        Timber.d("WebRtc service created")
        super.onCreate()
    }

    override fun retrieveController(): WebRtcServiceController = webRtcServiceManager

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("WebRtc service destroyed")
        webRtcServiceManager.destroy()
    }

    fun offerDevice(deviceUuid: String) {
        webRtcServiceManager.offerDevice(deviceUuid)
    }

    fun attachRemoteView(remoteView: SurfaceViewRenderer) {
        webRtcServiceManager.attachRemoteView(remoteView)
    }

    fun attachLocalView(localView: SurfaceViewRenderer) {
        webRtcServiceManager.attachLocalView(localView)
    }

    fun detachViews() {
        webRtcServiceManager.detachViews()
    }

    fun getRemoteUuid() = webRtcServiceManager.remoteUuid

    fun switchCamera() = webRtcServiceManager.switchCamera()

    fun enableCamera(isEnabled: Boolean) {
        webRtcServiceManager.enableCamera(isEnabled)
    }

    fun isCameraEnabled() = webRtcServiceManager.isCameraEnabled()

    fun enableMicrophone(isEnabled: Boolean) {
        webRtcServiceManager.enableMicrophone(isEnabled)
    }

    fun isMicrophoneEnabled() = webRtcServiceManager.isMicrophoneEnabled()

    inner class LocalBinder : Binder() {
        val service: WebRtcService
            get() = this@WebRtcService
    }
}


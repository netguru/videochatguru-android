package co.netguru.chatroulette.webrtc.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import co.netguru.chatroulette.app.App
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber
import javax.inject.Inject


class WebRtcService : Service() {

    @Inject lateinit var webRtcServiceManager: WebRtcServiceManager

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("WebRtc service created")
        App.getApplicationComponent(this).webRtcServiceComponent().inject(this)
    }

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

    inner class LocalBinder : Binder() {
        val service: WebRtcService
            get() = this@WebRtcService
    }
}


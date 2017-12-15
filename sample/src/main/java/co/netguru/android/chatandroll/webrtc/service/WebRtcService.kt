package co.netguru.android.chatandroll.webrtc.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import co.netguru.android.chatandroll.R
import co.netguru.android.chatandroll.app.App
import co.netguru.android.chatandroll.common.extension.getColorCompat
import co.netguru.android.chatandroll.feature.base.service.BaseServiceWithFacade
import co.netguru.android.chatandroll.feature.main.MainActivity
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber
import javax.inject.Inject


class WebRtcService : BaseServiceWithFacade<WebRtcServiceFacade, WebRtcServiceController>(), WebRtcServiceFacade {

    companion object {
        fun startService(packageContext: Context) {
            packageContext.startService(Intent(packageContext, WebRtcService::class.java))
        }

        fun bindService(context: Context, connection: ServiceConnection) {
            context.bindService(Intent(context, WebRtcService::class.java), connection, 0)
        }

        private val BACKGROUND_WORK_NOTIFICATION_ID = 1
        private val PENDING_INTENT_REQUEST_CODE = 1
    }

    @Inject lateinit var webRtcServiceController: WebRtcServiceController

    private val binder = LocalBinder()

    private val notificationManager by lazy {
        NotificationManagerCompat.from(this)
    }

    override fun onBind(intent: Intent) = binder

    override fun onCreate() {
        App.getApplicationComponent(this).webRtcServiceComponent().inject(this)
        Timber.d("WebRtc service created")
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideBackgroundWorkWarning()
    }

    override fun retrieveController(): WebRtcServiceController = webRtcServiceController

    override fun stop() = stopSelf()

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

    fun showBackgroundWorkWarning() {
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val pendingMainActivityIntent = PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, App.BACKGROUND_WORK_NOTIFICATIONS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_small_logo)
                .setContentTitle(getString(R.string.ongoing_call_notification_title))
                .setContentText(getString(R.string.ongoing_call_notification_text))
                .setColor(getColorCompat(R.color.accent))
                .setContentIntent(pendingMainActivityIntent)
                .setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .build()
        notificationManager.notify(BACKGROUND_WORK_NOTIFICATION_ID, notification)
    }

    fun hideBackgroundWorkWarning() {
        notificationManager.cancel(BACKGROUND_WORK_NOTIFICATION_ID)
    }

    inner class LocalBinder : Binder() {
        val service: WebRtcService
            get() = this@WebRtcService
    }
}


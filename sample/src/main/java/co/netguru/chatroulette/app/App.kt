package co.netguru.chatroulette.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatDelegate
import android.widget.Toast
import co.netguru.chatguru.disableWebRtcLogs
import co.netguru.chatguru.enableInternalWebRtclogs
import co.netguru.chatguru.enableWebRtcLogs
import co.netguru.chatroulette.BuildConfig
import co.netguru.chatroulette.R
import co.netguru.chatroulette.data.firebase.FirebaseModule
import com.squareup.leakcanary.LeakCanary
import org.webrtc.Logging
import timber.log.Timber
import java.util.*


class App : Application() {

    companion object Factory {

        val BACKGROUND_WORK_NOTIFICATIONS_CHANNEL_ID = "background_channel"

        val CURRENT_DEVICE_UUID = UUID.randomUUID().toString()

        fun get(context: Context): App = context.applicationContext as App

        fun getApplicationComponent(context: Context): ApplicationComponent =
                (context.applicationContext as App).applicationComponent
    }

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .firebaseModule(FirebaseModule())
                .build()
    }

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            //Enables WebRTC Logging
            enableWebRtcLogs(true)
            enableInternalWebRtclogs(Logging.Severity.LS_INFO)
            Toast.makeText(this, "Uuid: ${App.CURRENT_DEVICE_UUID}", Toast.LENGTH_LONG).show()
        } else {
            disableWebRtcLogs()
        }

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(BACKGROUND_WORK_NOTIFICATIONS_CHANNEL_ID,
                    getString(R.string.background_work_notifications_channel),
                    NotificationManagerCompat.IMPORTANCE_HIGH)
                    .apply {
                        description = getString(R.string.background_work_notification_channel_description)
                    }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
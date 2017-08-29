package co.netguru.chatroulette.app

import android.app.Application
import android.content.Context
import android.widget.Toast
import co.netguru.chatroulette.BuildConfig
import co.netguru.chatroulette.data.firebase.FirebaseModule
import co.netguru.simplewebrtc.WebRtcClient
import com.squareup.leakcanary.LeakCanary
import org.webrtc.Logging
import timber.log.Timber
import java.util.*

class App : Application() {

    companion object Factory {

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

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            //Enables WebRTC Logging
            WebRtcClient.enableSimpleWebRtcLogs(true)
            Toast.makeText(this, "Uuid: ${App.CURRENT_DEVICE_UUID}", Toast.LENGTH_LONG).show()
        }
    }
}
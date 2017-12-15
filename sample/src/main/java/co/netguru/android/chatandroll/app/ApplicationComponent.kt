package co.netguru.android.chatandroll.app

import co.netguru.android.chatandroll.data.firebase.FirebaseModule
import co.netguru.android.chatandroll.feature.main.video.VideoFragmentComponent
import co.netguru.android.chatandroll.webrtc.service.WebRtcServiceComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, FirebaseModule::class))
interface ApplicationComponent {

    fun videoFragmentComponent(): VideoFragmentComponent

    fun webRtcServiceComponent(): WebRtcServiceComponent
}

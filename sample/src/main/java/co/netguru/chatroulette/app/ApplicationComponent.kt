package co.netguru.chatroulette.app

import co.netguru.chatroulette.data.firebase.FirebaseModule
import co.netguru.chatroulette.feature.main.video.VideoFragmentComponent
import co.netguru.chatroulette.webrtc.service.WebRtcServiceComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, FirebaseModule::class))
interface ApplicationComponent {

    fun videoFragmentComponent(): VideoFragmentComponent

    fun webRtcServiceComponent(): WebRtcServiceComponent
}

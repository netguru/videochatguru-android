package co.netguru.chatroulette.app

import co.netguru.chatroulette.data.firebase.FirebaseModule
import co.netguru.chatroulette.data.net.NetModule
import co.netguru.chatroulette.feature.main.VideoFragmentComponent
import co.netguru.chatroulette.feature.main.MainComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, NetModule::class, FirebaseModule::class))
interface ApplicationComponent {

    fun getMainComponent(): MainComponent

    fun getVideoFragmentComponet(): VideoFragmentComponent
}

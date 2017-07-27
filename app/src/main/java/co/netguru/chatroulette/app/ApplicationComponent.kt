package co.netguru.chatroulette.app

import co.netguru.chatroulette.data.firebase.FirebaseModule
import co.netguru.chatroulette.feature.main.MainComponent
import co.netguru.chatroulette.feature.main.VideoFragmentComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, FirebaseModule::class))
interface ApplicationComponent {

    fun getMainComponent(): MainComponent

    fun getVideoFragmentComponet(): VideoFragmentComponent
}

package co.netguru.chatroulette.feature.main

import co.netguru.chatroulette.common.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface MainComponent {
    fun inject(mainActivity: MainActivity)

    fun mainPresenter(): MainPresenter
}
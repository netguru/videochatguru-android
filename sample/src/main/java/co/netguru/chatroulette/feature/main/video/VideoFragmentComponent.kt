package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.common.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface VideoFragmentComponent {
    fun inject(videoFragment: VideoFragment)

    fun videoFragmentPresenter(): VideoFragmentPresenter
}
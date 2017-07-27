package co.netguru.chatroulette.feature.main

import co.netguru.chatroulette.common.di.FragmentScope
import co.netguru.chatroulette.feature.main.video.VideoFragment
import co.netguru.chatroulette.feature.main.video.VideoFragmentPresenter
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface VideoFragmentComponent {
    fun inject(videoFragment: VideoFragment)

    fun videoFragmentPresenter(): VideoFragmentPresenter
}
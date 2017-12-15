package co.netguru.android.chatandroll.feature.main.video

import co.netguru.android.chatandroll.common.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface VideoFragmentComponent {
    fun inject(videoFragment: VideoFragment)

    fun videoFragmentPresenter(): VideoFragmentPresenter
}
package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.feature.base.MvpView

interface VideoFragmentView : MvpView {
    fun connectTo(uuid: String)

}
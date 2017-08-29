package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.feature.base.MvpView

interface VideoFragmentView : MvpView {
    fun connectTo(uuid: String)
    fun showCamViews()
    fun showStartRouletteView()
    fun disconnect()
    fun attachService()
    fun getRemoteUuid(): String?
    fun showErrorWhileChoosingRandom()
    fun showNoOneAvailable()
    fun showLookingForPartnerMessage()
    fun showOtherPartyFinished()
    fun showConnectedMsg()
    fun showWillTryToRestartMsg()
}
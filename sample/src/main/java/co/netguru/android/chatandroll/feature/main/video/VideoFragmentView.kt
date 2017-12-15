package co.netguru.android.chatandroll.feature.main.video

import co.netguru.android.chatandroll.feature.base.MvpView

interface VideoFragmentView : MvpView {
    val remoteUuid: String?

    fun connectTo(uuid: String)
    fun showCamViews()
    fun showStartRouletteView()
    fun disconnect()
    fun attachService()
    fun showErrorWhileChoosingRandom()
    fun showNoOneAvailable()
    fun showLookingForPartnerMessage()
    fun showOtherPartyFinished()
    fun showConnectedMsg()
    fun showWillTryToRestartMsg()
    fun hideConnectButtonWithAnimation()
}
package co.netguru.chatroulette.feature.main.video

import org.webrtc.SdpObserver

interface SdpCreateObserver : SdpObserver {

    override fun onSetFailure(error: String) {
        throw IllegalStateException("onSetFailure called in set listener")
    }

    override fun onSetSuccess() {
        throw IllegalStateException("onSetSuccess called in set listener")
    }
}
package co.netguru.simplewebrtc

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

interface SdpSetObserver : SdpObserver {

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        throw IllegalStateException("onCreateSuccess called in set listener")
    }

    override fun onCreateFailure(error: String) {
        throw IllegalStateException("onCreateFailure called in set listener")
    }
}

package co.netguru.chatroulette.webrtc

import org.webrtc.SessionDescription

interface WebRtcAnsweringPartyListener {
    fun onError(error: String)

    fun onSuccess(localSessionDescription: SessionDescription)
}
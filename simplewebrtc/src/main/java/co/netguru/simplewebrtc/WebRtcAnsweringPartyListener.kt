package co.netguru.simplewebrtc

import org.webrtc.SessionDescription

interface WebRtcAnsweringPartyListener {
    fun onError(error: String)

    fun onSuccess(localSessionDescription: SessionDescription)
}
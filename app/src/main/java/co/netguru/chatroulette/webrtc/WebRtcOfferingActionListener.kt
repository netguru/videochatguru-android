package co.netguru.chatroulette.webrtc

import org.webrtc.SessionDescription

interface WebRtcOfferingActionListener {
    fun onError(error: String)

    fun onOfferRemoteDescription(localSessionDescription: SessionDescription)

}
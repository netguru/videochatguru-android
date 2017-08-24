package co.netguru.simplewebrtc

import org.webrtc.SessionDescription

interface WebRtcOfferingActionListener {
    fun onError(error: String)

    fun onOfferRemoteDescription(localSessionDescription: SessionDescription)

}
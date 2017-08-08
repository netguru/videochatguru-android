package co.netguru.chatroulette.webrtc

import org.webrtc.IceCandidate


interface PeerConnectionListener {
    fun onIceCandidate(iceCandidate: IceCandidate)
    fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>)
}
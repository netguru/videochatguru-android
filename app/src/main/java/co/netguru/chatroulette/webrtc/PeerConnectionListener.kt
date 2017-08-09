package co.netguru.chatroulette.webrtc

import org.webrtc.IceCandidate
import org.webrtc.PeerConnection


interface PeerConnectionListener {
    fun onIceCandidate(iceCandidate: IceCandidate)
    fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>)
    fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState)
}
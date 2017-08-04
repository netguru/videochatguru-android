package co.netguru.chatroulette.feature.main.video

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription


interface PeerConnectionListener {
    fun onIceCandidate(iceCandidate: IceCandidate)
    fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>)
    fun onOffer(sessionDescription: SessionDescription)
    fun onAnswer(sessionDescription: SessionDescription)
}
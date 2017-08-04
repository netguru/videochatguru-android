package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.data.model.SessionDescriptionFirebase
import co.netguru.chatroulette.feature.base.MvpView
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection

interface VideoFragmentView : MvpView {
    fun handleRemoteOffer(remoteSessionDescription: SessionDescriptionFirebase)
    fun handleRemoteAnswer(answer: SessionDescriptionFirebase)
    fun addIceCandidate(iceCandidate: IceCandidate)
    fun removeIceCandidate(iceCandidate: IceCandidate)
    fun addIceServers(iceServers: List<PeerConnection.IceServer>)
    fun showServersRetrievingError()
    fun createOffer()

}
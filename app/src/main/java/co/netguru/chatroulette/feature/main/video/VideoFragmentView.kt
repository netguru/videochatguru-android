package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.data.model.SessionDescriptionFirebase
import co.netguru.chatroulette.feature.base.MvpView
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection

interface VideoFragmentView : MvpView {
    fun handleRemoteDescription(remoteSessionDescription: SessionDescriptionFirebase)
    fun handleAnswer(answer: SessionDescriptionFirebase)
    fun addIceCandidate(data: IceCandidate)
    fun removeIceCandidate(data: IceCandidate)
    fun addIceServers(it: List<PeerConnection.IceServer>)
    fun showServersRetrievingError()

}
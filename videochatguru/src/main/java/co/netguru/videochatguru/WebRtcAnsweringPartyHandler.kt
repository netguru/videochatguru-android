package co.netguru.videochatguru

import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription


internal class WebRtcAnsweringPartyHandler(
        private val peer: PeerConnection,
        private val offerAnswerConstraints: MediaConstraints,
        private val webRtcAnsweringPartyListener: WebRtcAnsweringPartyListener) {

    fun handleRemoteOffer(remoteSessionDescription: SessionDescription) {
        peer.setRemoteDescription(object : SdpSetObserver {
            override fun onSetSuccess() {
                createAnswer()
            }

            override fun onSetFailure(error: String) {
                webRtcAnsweringPartyListener.onError(error)
            }

        }, remoteSessionDescription)
    }

    private fun createAnswer() {
        peer.createAnswer(object : SdpCreateObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                setLocalAnswerDescription(sessionDescription)
            }

            override fun onCreateFailure(error: String) {
                webRtcAnsweringPartyListener.onError(error)
            }

        }, offerAnswerConstraints)
    }

    private fun setLocalAnswerDescription(sessionDescription: SessionDescription) {
        peer.setLocalDescription(object : SdpSetObserver {
            override fun onSetSuccess() {
                webRtcAnsweringPartyListener.onSuccess(sessionDescription)
            }

            override fun onSetFailure(error: String) {
                webRtcAnsweringPartyListener.onError(error)
            }
        }, sessionDescription)
    }
}
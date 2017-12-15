package co.netguru.videochatguru

import co.netguru.videochatguru.util.Logger
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription


internal class WebRtcOfferingPartyHandler(
        private val peer: PeerConnection,
        private val webRtcActionListener: WebRtcOfferingActionListener) {

    companion object {
        private val TAG = WebRtcOfferingPartyHandler::class.java.simpleName
    }

    fun createOffer(offerAnswerConstraints: MediaConstraints) {
        Logger.d(TAG, "Creating offer with $offerAnswerConstraints")
        peer.createOffer(object : SdpCreateObserver {
            override fun onCreateSuccess(localSessionDescription: SessionDescription) {
                setLocalOfferDescription(localSessionDescription)
            }

            override fun onCreateFailure(error: String) {
                webRtcActionListener.onError(error)
            }

        }, offerAnswerConstraints)
    }

    private fun setLocalOfferDescription(localSessionDescription: SessionDescription) {
        peer.setLocalDescription(object : SdpSetObserver {

            override fun onSetSuccess() {
                webRtcActionListener.onOfferRemoteDescription(localSessionDescription)
            }

            override fun onSetFailure(error: String) {
                webRtcActionListener.onError(error)
            }

        }, localSessionDescription)
    }

    fun handleRemoteAnswer(remoteSessionDescription: SessionDescription) {
        peer.setRemoteDescription(object : SdpSetObserver {
            override fun onSetSuccess() {
                Logger.d(TAG, "Remote description from answer set successfully")
            }

            override fun onSetFailure(error: String) {
                webRtcActionListener.onError(error)
            }

        }, remoteSessionDescription)
    }
}
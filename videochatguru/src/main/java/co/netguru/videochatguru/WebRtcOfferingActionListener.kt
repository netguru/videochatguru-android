package co.netguru.videochatguru

import org.webrtc.SessionDescription

/**
 * Listener used for notifying of offering party events
 */
interface WebRtcOfferingActionListener {
    /**
     * Triggered in case of internal errors.
     */
    fun onError(error: String)

    /**
     * Called when local session description from offering party is created.
     * [localSessionDescription] object should be sent to the other party through established connection channel.
     */
    fun onOfferRemoteDescription(localSessionDescription: SessionDescription)

}
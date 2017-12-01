package co.netguru.videochatguru

import org.webrtc.SessionDescription

/**
 * Listener used for notifying of answering party events
 */
interface WebRtcAnsweringPartyListener {
    /**
     * Triggered in case of internal errors.
     */
    fun onError(error: String)

    /**
     * Triggered when local session description from answering party is created.
     * [localSessionDescription] object should be sent to the other party through established connection channel.
     */
    fun onSuccess(localSessionDescription: SessionDescription)
}
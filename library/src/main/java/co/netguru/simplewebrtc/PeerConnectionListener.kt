package co.netguru.simplewebrtc

import org.webrtc.IceCandidate
import org.webrtc.PeerConnection


interface PeerConnectionListener {
    /**
     * Triggered whenever WebRTC finds ice candidate, [iceCandidate] object should be passed to the other party
     * through some established communication channel.
     */
    fun onIceCandidate(iceCandidate: IceCandidate)

    /**
     * Called whenever WebRTC ice candidates are removed, [iceCandidates] should be passed to the other party
     * through some established communication channel.
     */
    fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>)

    /**
     * Triggered when connection state changes. Can be one of [PeerConnection.IceConnectionState] enum values.
     */
    fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState)
}
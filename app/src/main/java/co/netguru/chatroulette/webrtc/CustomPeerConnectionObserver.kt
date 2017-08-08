package co.netguru.chatroulette.webrtc

import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import timber.log.Timber

open class CustomPeerConnectionObserver : PeerConnection.Observer {

    override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {
        Timber.d("onSignalingChange() called with $signalingState")
    }

    override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
        Timber.d("onIceConnectionChange() called with $iceConnectionState")
    }

    override fun onIceConnectionReceivingChange(b: Boolean) {
        Timber.d("onIceConnectionReceivingChange() called with $b")
    }

    override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {
        Timber.d(" onIceGatheringChange() called with $iceGatheringState")
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        Timber.d("onIceCandidate() called with $iceCandidate")
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
        Timber.d("onIceCandidatesRemoved called with $iceCandidates")
    }

    override fun onAddStream(mediaStream: MediaStream) {
        Timber.d("onAddStreamCalled() called with $mediaStream")
    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        Timber.d("onRemoveStream() called with $mediaStream")
    }

    override fun onDataChannel(dataChannel: DataChannel) {
        Timber.d("onDataChannel() called with  $dataChannel")
    }

    override fun onRenegotiationNeeded() {
        Timber.d("onRenegotiationNeeded() called")
    }
}

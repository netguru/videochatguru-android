package co.netguru.videochatguru

import co.netguru.videochatguru.util.Logger
import org.webrtc.*

internal interface CustomPeerConnectionObserver : PeerConnection.Observer {

    companion object {
        private val TAG = CustomPeerConnectionObserver::class.java.simpleName
    }

    override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {
        Logger.d(TAG, "onSignalingChange() called with $signalingState")
    }

    override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
        Logger.d(TAG, "onIceConnectionChange() called with $iceConnectionState")
    }

    override fun onIceConnectionReceivingChange(b: Boolean) {
        Logger.d(TAG, "onIceConnectionReceivingChange() called with $b")
    }

    override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {
        Logger.d(TAG, "onIceGatheringChange() called with $iceGatheringState")
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        Logger.d(TAG, "onIceCandidate() called with $iceCandidate")
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
        Logger.d(TAG, "onIceCandidatesRemoved called with $iceCandidates")
    }

    override fun onAddStream(mediaStream: MediaStream) {
        Logger.d(TAG, "onAddStreamCalled() called with $mediaStream")
    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        Logger.d(TAG, "onRemoveStream() called with $mediaStream")
    }

    override fun onDataChannel(dataChannel: DataChannel) {
        Logger.d(TAG, "onDataChannel() called with  $dataChannel")
    }

    override fun onRenegotiationNeeded() {
        Logger.d(TAG, "onRenegotiationNeeded() called")
    }

    override fun onAddTrack(rtpReceiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {
        Logger.d(TAG, "onAddTrack() called with args rtpReceiver:$rtpReceiver, mediaStreams: $mediaStreams")
    }
}

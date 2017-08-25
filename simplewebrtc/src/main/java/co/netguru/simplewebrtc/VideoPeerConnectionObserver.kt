package co.netguru.simplewebrtc

import co.netguru.simplewebrtc.util.Logger
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection


internal class VideoPeerConnectionObserver(
        private val peerConnectionListener: PeerConnectionListener,
        private val remoteVideoListener: RemoteVideoListener) : CustomPeerConnectionObserver {

    private val TAG = VideoPeerConnectionObserver::class.java.simpleName

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        super.onIceCandidate(iceCandidate)
        peerConnectionListener.onIceCandidate(iceCandidate)
    }

    override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
        super.onIceConnectionChange(iceConnectionState)
        peerConnectionListener.onIceConnectionChange(iceConnectionState)
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
        super.onIceCandidatesRemoved(iceCandidates)
        peerConnectionListener.onIceCandidatesRemoved(iceCandidates)
    }

    override fun onAddStream(mediaStream: MediaStream) {
        super.onAddStream(mediaStream)
        if (mediaStream.audioTracks.size > 1 || mediaStream.videoTracks.size > 1) {
            Logger.e(TAG, "Weird-looking stream: $mediaStream")
            return
        }
        if (mediaStream.videoTracks.size == 1) {
            mediaStream.preservedVideoTracks
            val remoteVideoTrack = mediaStream.videoTracks.first
            remoteVideoListener.onAddRemoteVideoStream(remoteVideoTrack)
        }

    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        super.onRemoveStream(mediaStream)
        remoteVideoListener.removeVideoStream()
    }
}
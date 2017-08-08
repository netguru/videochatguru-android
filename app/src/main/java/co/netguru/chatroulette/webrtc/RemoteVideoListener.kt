package co.netguru.chatroulette.webrtc

import org.webrtc.VideoTrack


interface RemoteVideoListener {
    fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack)
    fun removeVideoStream()
}
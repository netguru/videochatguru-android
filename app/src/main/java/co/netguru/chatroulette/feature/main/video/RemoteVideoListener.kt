package co.netguru.chatroulette.feature.main.video

import org.webrtc.VideoTrack


interface RemoteVideoListener {
    fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack)
    fun removeVideoStream()
}
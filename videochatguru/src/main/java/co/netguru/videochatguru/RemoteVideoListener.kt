package co.netguru.videochatguru

import org.webrtc.VideoTrack


internal interface RemoteVideoListener {

    fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack)

    fun removeVideoStream()
}
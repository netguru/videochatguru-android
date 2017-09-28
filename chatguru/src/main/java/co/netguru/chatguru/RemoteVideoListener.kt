package co.netguru.chatguru

import org.webrtc.VideoTrack


internal interface RemoteVideoListener {

    fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack)

    fun removeVideoStream()
}
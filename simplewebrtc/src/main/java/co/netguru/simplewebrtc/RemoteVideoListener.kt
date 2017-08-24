package co.netguru.simplewebrtc

import org.webrtc.VideoTrack


internal interface RemoteVideoListener {
    fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack)
    fun removeVideoStream()
}
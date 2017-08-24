package co.netguru.simplewebrtc

import org.webrtc.VideoTrack


interface RemoteVideoListener {
    fun onAddRemoteVideoStream(remoteVideoTrack: VideoTrack)
    fun removeVideoStream()
}
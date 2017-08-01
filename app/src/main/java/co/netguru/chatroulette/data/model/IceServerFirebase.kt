package co.netguru.chatroulette.data.model

import org.webrtc.PeerConnection


data class IceServerFirebase(val uri: String? = null, val username: String? = null, val password: String? = null) {

    fun toIceServer(): PeerConnection.IceServer {
        if (username == null || password == null) {
            return PeerConnection.IceServer(uri)
        } else {
            return PeerConnection.IceServer(uri, username, password)
        }
    }

}
package co.netguru.android.chatandroll.data.model

import org.webrtc.PeerConnection


@Suppress("MemberVisibilityCanPrivate")//Firebase model - members cant be private
data class IceServerFirebase(val uri: String? = null, val username: String? = null, val password: String? = null) {

    fun toIceServer(): PeerConnection.IceServer {
        return if (username == null || password == null) {
            PeerConnection.IceServer(uri)
        } else {
            PeerConnection.IceServer(uri, username, password)
        }
    }

}
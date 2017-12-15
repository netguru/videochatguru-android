package co.netguru.android.chatandroll.data.model

import org.webrtc.IceCandidate


data class IceCandidateFirebase(val sdpMLineIndex: Int? = null, val sdpMid: String? = null, val sdp: String? = null) {

    companion object {
        fun createFromIceCandidate(iceCandidate: IceCandidate): IceCandidateFirebase =
                IceCandidateFirebase(sdpMLineIndex = iceCandidate.sdpMLineIndex, sdpMid = iceCandidate.sdpMid, sdp = iceCandidate.sdp)
    }

    fun toIceCandidate() = IceCandidate(sdpMid, sdpMLineIndex ?: -1, sdp)

}
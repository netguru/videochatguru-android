package co.netguru.chatroulette.data.model

import org.webrtc.IceCandidate


data class IceCandidateFirebase(val sdpMLineIndex: Int? = null, val sdpMid: String? = null, val sdp: String? = null) {

    companion object {
        fun createFromIceCandidate(iceCandidate: IceCandidate): IceCandidateFirebase =
                IceCandidateFirebase(sdpMLineIndex = iceCandidate.sdpMLineIndex, sdpMid = iceCandidate.sdpMid, sdp = iceCandidate.sdp)

        fun createFromIceCandidates(iceCandidates: Array<IceCandidate>): List<IceCandidateFirebase> =
                iceCandidates.map { createFromIceCandidate(it) }
    }

    fun toIceCandidate() = IceCandidate(sdpMid, sdpMLineIndex ?: -1, sdp)

}
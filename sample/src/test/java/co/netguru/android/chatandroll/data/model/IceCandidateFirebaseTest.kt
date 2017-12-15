package co.netguru.android.chatandroll.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("IllegalIdentifier")
class IceCandidateFirebaseTest {

    companion object {
        private const val SDP_MLINE_INDEX = 1
        private const val SDP_MID = "test:sdp_mid"
        private const val SDP = "test:sdp"
    }

    @Test
    fun `should return proper object when toIceCandidate is called`() {
        //given
        val iceCandidateFirebase = IceCandidateFirebase(SDP_MLINE_INDEX, SDP_MID, SDP)
        //when
        val result = iceCandidateFirebase.toIceCandidate()
        //then
        assertEquals(result.sdp, SDP)
        assertEquals(result.sdpMid, SDP_MID)
        assertEquals(result.sdpMLineIndex, SDP_MLINE_INDEX)
    }
}
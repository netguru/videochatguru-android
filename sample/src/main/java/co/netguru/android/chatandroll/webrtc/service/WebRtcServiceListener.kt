package co.netguru.android.chatandroll.webrtc.service

import org.webrtc.PeerConnection


interface WebRtcServiceListener {

    /**
     * When receiving this exception service is in unrecoverable state and will call stopSelf, bound view(if any) should unbind
     */
    fun criticalWebRTCServiceException(throwable: Throwable)

    fun connectionStateChange(iceConnectionState: PeerConnection.IceConnectionState)
}
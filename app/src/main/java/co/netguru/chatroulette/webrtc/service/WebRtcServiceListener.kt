package co.netguru.chatroulette.webrtc.service


interface WebRtcServiceListener {

    /**
     * When receiving this exception service is in unrecoverable state and will call stopSelf, bound view(if any) should unbind
     */
    fun criticalWebRTCServiceException(throwable: Throwable)
}
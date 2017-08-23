package co.netguru.chatroulette.webrtc.service


interface WebRtcServiceListener {

    /**
     * When receiving this exception service will call stopSelf 
     */
    fun criticalException() {

    }
}
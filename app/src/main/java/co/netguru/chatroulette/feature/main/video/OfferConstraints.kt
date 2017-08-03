package co.netguru.chatroulette.feature.main.video

/**
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/master/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
enum class OfferConstraints(override val constraintString: String) : WebRtcConstraint {

    DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT("DtlsSrtpKeyAgreement"),
    OFFER_TO_RECEIVE_AUDIO("OfferToReceiveAudio"),
    OFFER_TO_RECEIVE_VIDEO("OfferToReceiveVideo");

}
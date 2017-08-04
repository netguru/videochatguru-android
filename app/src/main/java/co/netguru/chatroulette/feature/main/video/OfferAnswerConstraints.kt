package co.netguru.chatroulette.feature.main.video

/**
 * Constraint keys for CreateOffer / CreateAnswer defined in W3C specification.
 *
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/e33c5d918a213202321bde751226c4949644fe5e/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
enum class OfferAnswerConstraints(override val constraintString: String) : WebRtcConstraint {

    OFFER_TO_RECEIVE_AUDIO("OfferToReceiveAudio"),
    OFFER_TO_RECEIVE_VIDEO("OfferToReceiveVideo");

}
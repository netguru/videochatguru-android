package co.netguru.videochatguru.constraints

/**
 * Constraint keys for CreateOffer / CreateAnswer defined in W3C specification.
 *
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/e33c5d918a213202321bde751226c4949644fe5e/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
enum class OfferAnswerConstraints(override val constraintString: String) : WebRtcConstraint<Boolean> {

    OFFER_TO_RECEIVE_AUDIO("OfferToReceiveAudio"),
    OFFER_TO_RECEIVE_VIDEO("OfferToReceiveVideo"),
    /**
     * Many codec's and systems are capable of detecting "silence" and changing their behavior in this
     * case by doing things such as not transmitting any media. In many cases, such as when dealing
     * with emergency calling or sounds other than spoken voice, it is desirable to be able to turn
     * off this behavior. This option allows the application to provide information about whether it
     * wishes this type of processing enabled or disabled.
     */
    VOICE_ACTIVITY_DETECTION("VoiceActivityDetection"),
    /**
     * Tries to restart connection after it was in failed or disconnected state
     */
    ICE_RESTART("IceRestart"),
    /**
     * Google specific constraint for BUNDLE enable/disable.
     */
    GOOG_USE_RTP_MUX("googUseRtpMUX")

}
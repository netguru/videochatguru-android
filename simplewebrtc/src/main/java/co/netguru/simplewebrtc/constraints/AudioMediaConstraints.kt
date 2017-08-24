package co.netguru.simplewebrtc.constraints

/**
 * Audio constraints.
 *
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/e33c5d918a213202321bde751226c4949644fe5e/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
internal enum class AudioMediaConstraints(override val constraintString: String) : WebRtcConstraint {

    DISABLE_AUDIO_PROCESSING("echoCancellation"),
    ECHO_CANCELLATION("googEchoCancellation"),
    AUTO_GAIN_CONTROL("googAutoGainControl"),
    NOISE_SUPPRESSION("googNoiseSuppression"),
    INTELLIGIBILITY_ENHANCER("intelligibilityEnhancer"),
    HIGH_PASS_FILTER("googHighpassFilter"),
    TYPING_NOISE_DETECTION("googTypingNoiseDetection"),
    AUDIO_LEVEL_CONTROL_CONSTRAINT("levelControl");
}
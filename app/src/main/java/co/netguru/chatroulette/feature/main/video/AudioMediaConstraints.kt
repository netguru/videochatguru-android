package co.netguru.chatroulette.feature.main.video

/**
 * Audio constraints.
 *
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/e33c5d918a213202321bde751226c4949644fe5e/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
enum class AudioMediaConstraints(override val constraintString: String) : WebRtcConstraint {

    AUDIO_ECHO_CANCELLATION_CONSTRAINT("googEchoCancellation"),
    AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT("googAutoGainControl"),
    AUDIO_NOISE_SUPPRESSION_CONSTRAINT("googNoiseSuppression"),
    AUDIO_HIGH_PASS_FILTER_CONSTRAINT("googHighpassFilter"),
    AUDIO_TYPING_NOISE_DETECTION("googTypingNoiseDetection"),
    AUDIO_LEVEL_CONTROL_CONSTRAINT("levelControl");

}

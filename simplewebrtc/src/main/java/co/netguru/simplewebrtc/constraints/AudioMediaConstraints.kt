package co.netguru.simplewebrtc.constraints

/**
 * Audio constraints.
 *
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/e33c5d918a213202321bde751226c4949644fe5e/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
sealed class AudioMediaConstraints {
    enum class Booleans(override val constraintString: String) : WebRtcConstraint<Boolean> {
        DISABLE_AUDIO_PROCESSING("echoCancellation"),
        ECHO_CANCELLATION("googEchoCancellation"),
        ECHO_CANCELLATION_2("googEchoCancellation2"),
        DELAY_AGNOSTIC_ECHO_CANCELATION("googDAEchoCancellation"),
        AUTO_GAIN_CONTROL("googAutoGainControl"),
        AUTO_GAIN_CONTROL_2("googAutoGainControl2"),
        NOISE_SUPPRESSION("googNoiseSuppression"),
        NOISE_SUPPRESSION_2("googNoiseSuppression2"),
        INTELLIGIBILITY_ENHANCER("intelligibilityEnhancer"),
        LEVEL_CONTROL("levelControl"),
        HIGH_PASS_FILTER("googHighpassFilter"),
        TYPING_NOISE_DETECTION("googTypingNoiseDetection"),
        AUDIO_MIRRORING("googAudioMirroring")
    }

    enum class Integers(override val constraintString: String) : WebRtcConstraint<Int> {
        LEVEL_CONTROL_INITIAL_PEAK_LEVEL_DBFS("levelControlInitialPeakLevelDBFS"),
    }
}
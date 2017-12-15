package co.netguru.videochatguru.constraints

/**
 * Integer value based audio constraints.
 *
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/e33c5d918a213202321bde751226c4949644fe5e/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
enum class IntegerAudioConstraints(override val constraintString: String) : WebRtcConstraint<Int> {
    LEVEL_CONTROL_INITIAL_PEAK_LEVEL_DBFS("levelControlInitialPeakLevelDBFS"),
}
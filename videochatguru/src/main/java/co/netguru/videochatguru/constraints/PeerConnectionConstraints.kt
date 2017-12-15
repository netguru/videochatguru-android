package co.netguru.videochatguru.constraints

/**
 * These constraints should be used during PeerConnection construction.
 *
 * @see <a href="https://chromium.googlesource.com/external/webrtc/+/e33c5d918a213202321bde751226c4949644fe5e/webrtc/api/mediaconstraintsinterface.cc">
 *     Available constraints in media constraints interface implementation</a>
 */
enum class PeerConnectionConstraints(override val constraintString: String) : WebRtcConstraint<Boolean> {
    /**
     * Enabling allows to stream between firefox and chrome
     */
    DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT("DtlsSrtpKeyAgreement"),
    ENABLE_RTP_DATA_CHANNELS("RtpDataChannels"),
    /**
     * Differentiated Services Code Point - DiffServ is a coarse-grained, class-based mechanism for traffic management.
     * @see <a href="https://en.wikipedia.org/wiki/Differentiated_services">DSCP</a>
     */
    GOOG_DSCP("googDscp"),
    GOOG_IPV6("googIPv6"),
    /**
     *  Video stops as soon as you don't have enough bandwidth for the video.
     */
    GOOG_SUSPEND_VIDEO("googSuspendBelowMinBitrate"),
    GOOG_COMBINED_AUDIO_VIDEO_BWE("googCombinedAudioVideoBwe"),
    /**
     * Allow to reduce video quality when cpu usage is high
     */
    GOOG_CPU_OVERUSE_DETECTION("googCpuOveruseDetection"),
    GOOG_PAYLOAD_PADDING("googPayloadPadding")
}
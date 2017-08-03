package co.netguru.chatroulette.feature.main.video

import org.webrtc.MediaConstraints


interface WebRtcConstraint {

    val constraintString: String

    fun toKeyValuePair(enabled: Boolean) = MediaConstraints.KeyValuePair(constraintString, enabled.toString())

}
package co.netguru.chatroulette.webrtc.constraints

import org.webrtc.MediaConstraints


internal interface WebRtcConstraint {

    val constraintString: String

    fun toKeyValuePair(enabled: Boolean) = MediaConstraints.KeyValuePair(constraintString, enabled.toString())

}
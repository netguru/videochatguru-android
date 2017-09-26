package co.netguru.chatguru.constraints

import org.webrtc.MediaConstraints

interface WebRtcConstraint<in T> {

    val constraintString: String

    fun toKeyValuePair(value: T) = MediaConstraints.KeyValuePair(constraintString, value.toString())
}
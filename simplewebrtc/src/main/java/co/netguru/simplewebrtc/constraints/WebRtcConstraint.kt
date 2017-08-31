package co.netguru.simplewebrtc.constraints

import org.webrtc.MediaConstraints


interface WebRtcConstraint<T> {

    val constraintString: String

    fun toKeyValuePair(value: T) = MediaConstraints.KeyValuePair(constraintString, value.toString())
}
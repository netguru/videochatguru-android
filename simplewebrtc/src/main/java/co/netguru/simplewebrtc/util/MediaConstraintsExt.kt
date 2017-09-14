package co.netguru.simplewebrtc.util

import co.netguru.simplewebrtc.constraints.WebRtcConstraints
import org.webrtc.MediaConstraints


fun MediaConstraints.addConstraints(constraints: WebRtcConstraints<*, *>) {
    mandatory.addAll(constraints.mandatoryKeyValuePairs)
    optional.addAll(constraints.optionalKeyValuePairs)
}

fun MediaConstraints.addConstraints(vararg constraints: WebRtcConstraints<*, *>) {
    constraints.forEach {
        addConstraints(it)
    }
}
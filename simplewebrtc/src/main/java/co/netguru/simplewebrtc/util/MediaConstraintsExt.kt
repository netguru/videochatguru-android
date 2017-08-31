package co.netguru.simplewebrtc.util

import co.netguru.simplewebrtc.constraints.WebRtcConstraints
import org.webrtc.MediaConstraints


fun MediaConstraints.addConstraints(constraints: WebRtcConstraints<*, *>) {
    mandatory.addAll(constraints.getMandatoryKeyValuePairs())
    optional.addAll(constraints.getOptionalKeyValuePairs())
}

fun MediaConstraints.addConstraints(vararg constraints: WebRtcConstraints<*, *>) {
    constraints.forEach {
        addConstraints(it)
    }
}
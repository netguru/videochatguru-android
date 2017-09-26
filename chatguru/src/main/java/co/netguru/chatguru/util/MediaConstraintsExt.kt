package co.netguru.chatguru.util

import co.netguru.chatguru.constraints.WebRtcConstraints
import org.webrtc.MediaConstraints


internal fun MediaConstraints.addConstraints(constraints: WebRtcConstraints<*, *>) {
    mandatory.addAll(constraints.mandatoryKeyValuePairs)
    optional.addAll(constraints.optionalKeyValuePairs)
}

internal fun MediaConstraints.addConstraints(vararg constraints: WebRtcConstraints<*, *>) {
    constraints.forEach {
        addConstraints(it)
    }
}
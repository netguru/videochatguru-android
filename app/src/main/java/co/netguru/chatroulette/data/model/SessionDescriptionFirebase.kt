package co.netguru.chatroulette.data.model

import co.netguru.chatroulette.app.App
import org.webrtc.SessionDescription


data class SessionDescriptionFirebase(var senderUuid: String = App.DEVICE_UUID,
                                      var type: SessionDescription.Type? = null,
                                      var description: String? = null) {
    companion object {
        fun fromSessionDescriptionWithDefaultSenderUuid(sessionDescription: SessionDescription): SessionDescriptionFirebase {
            return SessionDescriptionFirebase(type = sessionDescription.type, description = sessionDescription.description)
        }
    }

    fun toSessionDescription() = SessionDescription(type, description)
}
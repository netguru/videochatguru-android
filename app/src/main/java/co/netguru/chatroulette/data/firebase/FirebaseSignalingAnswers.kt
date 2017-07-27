package co.netguru.chatroulette.data.firebase

import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.common.extension.DataChangeEvent
import co.netguru.chatroulette.common.extension.rxValueEvents
import co.netguru.chatroulette.data.model.SessionDescriptionFirebase
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.webrtc.SessionDescription
import javax.inject.Inject


class FirebaseSignalingAnswers @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    companion object {
        private const val ANSWERS_PATH = "answers/"
    }

    private fun deviceAnswersPath(deviceUuid: String) = ANSWERS_PATH.plus(deviceUuid)

    fun create(recipientUuid: String, remoteDescription: SessionDescription): Completable = Completable.create {
        val reference = firebaseDatabase.getReference(deviceAnswersPath(recipientUuid))
        reference.onDisconnect().removeValue()
        reference.setValue(SessionDescriptionFirebase.fromSessionDescriptionWithDefaultSenderUuid(remoteDescription))
        it.onComplete()
    }

    fun listen(): Flowable<DataChangeEvent<SessionDescriptionFirebase?>> {
        return Single.just { firebaseDatabase.getReference(deviceAnswersPath(App.CURRENT_DEVICE_UUID)) }
                .flatMapPublisher { it().rxValueEvents(SessionDescriptionFirebase::class.java) }
    }
}
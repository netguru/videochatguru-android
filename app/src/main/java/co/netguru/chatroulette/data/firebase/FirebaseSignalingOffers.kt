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


class FirebaseSignalingOffers @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    companion object {
        private const val OFFERS_PATH = "offers/"
    }

    private fun deviceOffersPath(deviceUuid: String) = OFFERS_PATH.plus(deviceUuid)

    fun create(recipientUuid: String, localSessionDescription: SessionDescription): Completable = Completable.create {
        val reference = firebaseDatabase.getReference(deviceOffersPath(recipientUuid))
        reference.onDisconnect().removeValue()
        reference.setValue(SessionDescriptionFirebase.fromSessionDescriptionWithDefaultSenderUuid(localSessionDescription))
        it.onComplete()
    }

    fun listen(): Flowable<DataChangeEvent<SessionDescriptionFirebase?>> {
        return Single.just { firebaseDatabase.getReference(deviceOffersPath(App.CURRENT_DEVICE_UUID)) }
                .flatMapPublisher { it().rxValueEvents(SessionDescriptionFirebase::class.java) }
    }
}
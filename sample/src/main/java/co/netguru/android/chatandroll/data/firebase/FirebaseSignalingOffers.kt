package co.netguru.android.chatandroll.data.firebase

import co.netguru.android.chatandroll.app.App
import co.netguru.android.chatandroll.common.extension.rxValueEvents
import co.netguru.android.chatandroll.data.model.SessionDescriptionFirebase
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toMaybe
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

    fun listenForNewOffersWithUuid(): Flowable<Pair<SessionDescription, String>> {
        return Single.just { firebaseDatabase.getReference(deviceOffersPath(App.CURRENT_DEVICE_UUID)) }
                .flatMapPublisher { it().rxValueEvents(SessionDescriptionFirebase::class.java) }
                .flatMapMaybe { it.data.toMaybe() }
                .map { Pair(it.toSessionDescription(), it.senderUuid) }
    }
}
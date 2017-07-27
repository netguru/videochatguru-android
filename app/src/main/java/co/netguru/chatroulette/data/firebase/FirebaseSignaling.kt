package co.netguru.chatroulette.data.firebase

import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.common.util.createFlowable
import co.netguru.chatroulette.data.model.RouletteConnection
import co.netguru.chatroulette.data.model.SessionDescriptionFirebase
import com.google.firebase.database.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Maybe
import org.webrtc.SessionDescription
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSignaling @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun connectAndRetrieveRandomDevice(): Maybe<String> = Completable.create {
        firebaseDatabase.goOnline()
        val firebaseOnlineReference = firebaseDatabase.getReference("online_devices/${App.DEVICE_UUID}")
        with(firebaseOnlineReference) {
            onDisconnect().removeValue()
            setValue(RouletteConnection())
        }
        it.onComplete()
    }.andThen(chooseRandomDevice())

    fun disconnect(): Completable = Completable.create {
        firebaseDatabase.goOffline()

        it.onComplete()
    }

    fun chooseRandomDevice(): Maybe<String> = Maybe.create {
        var lastUuid: String? = null

        firebaseDatabase.getReference("online_devices").runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                lastUuid = null
                val genericTypeIndicator = object : GenericTypeIndicator<MutableMap<String, RouletteConnection>>() {}
                val connections = mutableData.getValue(genericTypeIndicator) ?: return Transaction.success(mutableData)

                Timber.d("Fireabase transaction connections data : $connections")
                with(connections) {
                    val removedValue = remove(App.DEVICE_UUID)
                    Timber.d("Own uuid removed? $removedValue")
                    if (removedValue != null) {
                        val count = count()
                        if (count > 0) {
                            val random = SecureRandom().nextInt(count)
                            val remoteUuidToRemove = keys.toList()[random]
                            Timber.d("Device number $random from $count devices was choosen ")
                            remove(remoteUuidToRemove)
                            //if all went fine we replace data
                            mutableData.value = connections
                            Timber.d("Passed mutable data $mutableData")
                            lastUuid = remoteUuidToRemove
                        }
                    }
                }

                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, completed: Boolean, p2: DataSnapshot?) {
                if (databaseError != null) {
                    it.onError(databaseError.toException())
                } else if (completed && lastUuid != null) {
                    it.onSuccess(lastUuid as String)
                }
                it.onComplete()
            }
        })
    }

    fun createOffer(uuid: String, localDescription: SessionDescription) = Completable.create {
        val reference = firebaseDatabase.getReference("offers/$uuid")
        reference.onDisconnect().removeValue()
        reference.setValue(SessionDescriptionFirebase.fromSessionDescriptionWithDefaultSenderUuid(localDescription))
        it.onComplete()
    }

    fun listenForOffers() = createFlowable<SessionDescriptionFirebase>(BackpressureStrategy.BUFFER) {
        val reference = firebaseDatabase.getReference("offers/${App.DEVICE_UUID}")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                //todo
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val remoteDescriptionFirebase = dataSnapshot.getValue(SessionDescriptionFirebase::class.java)
                if (remoteDescriptionFirebase != null) {
                    it.onNext(remoteDescriptionFirebase)
                }
            }

        })
    }

    fun createAnswer(uuid: String, remoteDescription: SessionDescription) = Completable.create {
        val reference = firebaseDatabase.getReference("answers/$uuid")
        reference.onDisconnect().removeValue()
        reference.setValue(SessionDescriptionFirebase.fromSessionDescriptionWithDefaultSenderUuid(remoteDescription))
        it.onComplete()
    }

    fun listenForAnswers() = createFlowable<SessionDescriptionFirebase>(BackpressureStrategy.BUFFER) {
        val reference = firebaseDatabase.getReference("answers/${App.DEVICE_UUID}")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                //todo
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val remoteDescriptionFirebase = dataSnapshot.getValue(SessionDescriptionFirebase::class.java)
                if (remoteDescriptionFirebase != null) {
                    it.onNext(remoteDescriptionFirebase)
                }
            }

        })
    }
}
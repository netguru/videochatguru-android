package co.netguru.chatroulette.data.firebase

import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.common.util.ChildEvent
import co.netguru.chatroulette.common.util.ChildEventAdded
import co.netguru.chatroulette.common.util.ChildEventRemoved
import co.netguru.chatroulette.common.util.rxChildEvents
import co.netguru.chatroulette.data.model.IceCandidateFirebase
import com.google.firebase.database.*
import io.reactivex.Completable
import io.reactivex.Flowable
import org.webrtc.IceCandidate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseIceHandlers @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun sendIceCandidate(iceCandidateFirebase: IceCandidateFirebase): Completable {
        return Completable.create {
            val reference = firebaseDatabase.getReference("ice/${App.DEVICE_UUID}")
            with(reference) {
                onDisconnect().removeValue()
                push().setValue(iceCandidateFirebase)
            }
            it.onComplete()
        }
    }

    fun removeIceCandidates(iceCandidatesToRemoveFirebase: List<IceCandidateFirebase>): Completable {
        return Completable.create {
            val iceCandidatesToRemoveList = iceCandidatesToRemoveFirebase.toMutableList()
            val reference = firebaseDatabase.getReference("ice/${App.DEVICE_UUID}")
            reference.runTransaction(object : Transaction.Handler {

                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val typeIndicator = object : GenericTypeIndicator<MutableMap<String, IceCandidateFirebase>>() {}
                    val iceMap = mutableData.getValue(typeIndicator) ?:
                            return Transaction.success(mutableData)

                    for ((key, value) in iceMap) {
                        if (iceCandidatesToRemoveList.remove(value)) {
                            iceMap.remove(key)
                        }
                    }
                    mutableData.value = iceMap
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError, committed: Boolean, p2: DataSnapshot?) {
                    if (committed) {
                        it.onComplete()
                    } else {
                        it.onError(databaseError.toException())
                    }
                }

            })
        }
    }

    fun getIceCandidates(remoteUuid: String): Flowable<ChildEvent<IceCandidate>> {
        return firebaseDatabase.getReference("ice/$remoteUuid").rxChildEvents()
                .filter { it is ChildEventAdded || it is ChildEventRemoved }
                .map {
                    val iceCandidateFirebase: IceCandidateFirebase = it.data.getValue(IceCandidateFirebase::class.java) as IceCandidateFirebase
                    val iceCandidate = iceCandidateFirebase.toIceCandidate()
                    if (it is ChildEventAdded) {
                        ChildEventAdded(iceCandidate, it.previousChildName)
                    } else {
                        ChildEventRemoved(iceCandidate)
                    }
                }
    }

}

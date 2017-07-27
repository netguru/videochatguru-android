package co.netguru.chatroulette.common.util

import com.google.firebase.database.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Maybe
import io.reactivex.Single

interface ChildEvent<out T> {
    val data: T
}

data class ChildEventMoved<out T>(override val data: T, val previousChildName: String?) : ChildEvent<T>

data class ChildEventChanged<out T>(override val data: T, val previousChildName: String?) : ChildEvent<T>

data class ChildEventAdded<out T>(override val data: T, val previousChildName: String?) : ChildEvent<T>

data class ChildEventRemoved<out T>(override val data: T) : ChildEvent<T>

fun DatabaseReference.rxChildEvents() = createFlowable<ChildEvent<DataSnapshot>>(BackpressureStrategy.BUFFER) {

    val listener = object : ChildEventListener {
        override fun onChildMoved(ds: DataSnapshot, previousChildName: String?) {
            it.onNext(ChildEventMoved(ds, previousChildName))
        }

        override fun onChildChanged(ds: DataSnapshot, previousChildName: String?) {
            it.onNext(ChildEventChanged(ds, previousChildName))
        }

        override fun onChildAdded(ds: DataSnapshot, previousChildName: String?) {
            it.onNext(ChildEventAdded(ds, previousChildName))
        }

        override fun onChildRemoved(ds: DataSnapshot) {
            it.onNext(ChildEventRemoved(ds))
        }

        override fun onCancelled(error: DatabaseError) {
            it.onError(error.toException())
        }
    }
    it.setCancellable { removeEventListener(listener) }

    addChildEventListener(listener)
}

fun <T> DatabaseReference.rxChildEvents(genericTypeIndicator: GenericTypeIndicator<T>)
        = createFlowable<ChildEvent<T?>>(BackpressureStrategy.BUFFER) {

    val listener = object : ChildEventListener {
        override fun onChildMoved(ds: DataSnapshot, previousChildName: String?) {
            val data = ds.getValue(genericTypeIndicator)
            it.onNext(ChildEventMoved(data, previousChildName))
        }

        override fun onChildChanged(ds: DataSnapshot, previousChildName: String?) {
            val data = ds.getValue(genericTypeIndicator)
            it.onNext(ChildEventChanged(data, previousChildName))
        }

        override fun onChildAdded(ds: DataSnapshot, previousChildName: String?) {
            val data = ds.getValue(genericTypeIndicator)
            it.onNext(ChildEventAdded(data, previousChildName))
        }

        override fun onChildRemoved(ds: DataSnapshot) {
            val data = ds.getValue(genericTypeIndicator)
            it.onNext(ChildEventRemoved(data))
        }

        override fun onCancelled(error: DatabaseError) {
            it.onError(error.toException())
        }
    }
    it.setCancellable { removeEventListener(listener) }
}


fun DatabaseReference.rxSingleValue(): Single<DataSnapshot> = Single.create {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            it.onError(databaseError.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            it.onSuccess(dataSnapshot)
        }

    })
}

fun <T> DatabaseReference.rxSingleValue(clazz: Class<T>): Maybe<T> = rxSingleValue()
        .flatMapMaybe {
            val data = it.getValue(clazz)
            if (data == null) Maybe.empty() else Maybe.just(data)
        }

fun <T> DatabaseReference.rxSingleValue(genericTypeIndicator: GenericTypeIndicator<T>): Maybe<T> = rxSingleValue()
        .flatMapMaybe {
            val data = it.getValue(genericTypeIndicator)
            if (data == null) Maybe.empty() else Maybe.just(data)
        }

package co.netguru.android.chatandroll.common.extension

import co.netguru.android.chatandroll.common.util.RxUtils
import com.google.firebase.database.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface ChildEvent<out T> {
    val data: T
}

data class ChildEventMoved<out T>(override val data: T, val previousChildName: String?) : ChildEvent<T>

data class ChildEventChanged<out T>(override val data: T, val previousChildName: String?) : ChildEvent<T>

data class ChildEventAdded<out T>(override val data: T, val previousChildName: String?) : ChildEvent<T>

data class ChildEventRemoved<out T>(override val data: T) : ChildEvent<T>

data class DataChangeEvent<out T>(override val data: T) : ChildEvent<T>

fun DatabaseReference.rxChildEvents() = RxUtils.createFlowable<ChildEvent<DataSnapshot>>(BackpressureStrategy.BUFFER) {

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
        = RxUtils.createFlowable<ChildEvent<T?>>(BackpressureStrategy.BUFFER) {

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

    addChildEventListener(listener)
}


fun DatabaseReference.rxSingleValue(): Single<DataSnapshot> = Single.create {
    val listener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            it.onError(databaseError.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            it.onSuccess(dataSnapshot)
        }

    }
    it.setCancellable { removeEventListener(listener) }

    addListenerForSingleValueEvent(listener)
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

fun DatabaseReference.rxValueEvents() = RxUtils.createFlowable<DataSnapshot>(BackpressureStrategy.BUFFER) {
    val listener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            it.onError(databaseError.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            it.onNext(dataSnapshot)
        }

    }
    it.setCancellable { removeEventListener(listener) }

    addValueEventListener(listener)
}

fun <T> DatabaseReference.rxValueEvents(clazz: Class<T>): Flowable<DataChangeEvent<T?>> = rxValueEvents()
        .map {
            val data = it.getValue(clazz)
            DataChangeEvent(data)
        }

fun <T> DatabaseReference.rxValueEvents(genericTypeIndicator: GenericTypeIndicator<T>): Flowable<DataChangeEvent<T?>> = rxValueEvents()
        .map {
            val data = it.getValue(genericTypeIndicator)
            DataChangeEvent(data)
        }
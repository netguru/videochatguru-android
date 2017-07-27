package co.netguru.chatroulette.common.util

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

fun <T> createFlowable(mode: BackpressureStrategy, source: (FlowableEmitter<T>) -> Unit): Flowable<T> {
    return Flowable.create(source, mode)
}
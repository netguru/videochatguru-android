package co.netguru.chatroulette.feature.main

import co.netguru.chatroulette.common.util.applyCompletableIoSchedulers
import co.netguru.chatroulette.common.util.applyMaybeIoSchedulers
import co.netguru.chatroulette.data.firebase.FirebaseSignaling
import co.netguru.chatroulette.feature.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject


class MainPresenter @Inject constructor(private val firebaseSignaling: FirebaseSignaling) : BasePresenter<MainView>() {

    private val disposables = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        disposables.dispose()
    }

    fun startSearching() {
        Timber.d("Start searching")
        disposables += firebaseSignaling.connectAndRetrieveRandomDevice()
                .compose(applyMaybeIoSchedulers())
                .subscribeBy(
                        onSuccess = {
                            Timber.d("Next $it")
                            mvpView?.passOfferDevice(it)
                        },
                        onError = { Timber.e(it, "Error while chosing random") },
                        onComplete = { Timber.d("Done") }
                )
    }

    fun stop() {
        disposables += firebaseSignaling.disconnect()
                .compose(applyCompletableIoSchedulers())
                .subscribeBy(
                        onError = {
                            Timber.d(it)
                        },
                        onComplete = {}
                )
    }

}
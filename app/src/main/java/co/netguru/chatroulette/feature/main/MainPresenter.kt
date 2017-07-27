package co.netguru.chatroulette.feature.main

import co.netguru.chatroulette.common.util.RxUtils
import co.netguru.chatroulette.data.firebase.FirebaseSignalingOnline
import co.netguru.chatroulette.feature.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject


class MainPresenter @Inject constructor(private val firebaseSignalingOnline: FirebaseSignalingOnline) : BasePresenter<MainView>() {

    private val disposables = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        disposables.dispose()
    }

    fun startSearching() {
        Timber.d("Start searching")
        disposables += firebaseSignalingOnline.connectAndRetrieveRandomDevice()
                .compose(RxUtils.applyMaybeIoSchedulers())
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
        disposables += firebaseSignalingOnline.disconnect()
                .compose(RxUtils.applyCompletableIoSchedulers())
                .subscribeBy(
                        onError = {
                            Timber.d(it)
                        },
                        onComplete = {}
                )
    }

}
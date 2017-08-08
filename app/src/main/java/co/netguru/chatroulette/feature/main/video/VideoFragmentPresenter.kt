package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.common.util.RxUtils
import co.netguru.chatroulette.data.firebase.FirebaseSignalingOnline
import co.netguru.chatroulette.feature.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject


class VideoFragmentPresenter @Inject constructor(
        private val firebaseSignalingOnline: FirebaseSignalingOnline) : BasePresenter<VideoFragmentView>() {

    private val disposables by lazy { CompositeDisposable() }

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
                            mvpView?.connectTo(it)
                        },
                        onError = { Timber.e(it, "Error while choosing random") },
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
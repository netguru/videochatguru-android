package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.common.util.ChildEventAdded
import co.netguru.chatroulette.common.util.applyCompletableIoSchedulers
import co.netguru.chatroulette.common.util.applyFlowableIoSchedulers
import co.netguru.chatroulette.data.firebase.FirebaseIceHandlers
import co.netguru.chatroulette.data.firebase.FirebaseIceServers
import co.netguru.chatroulette.data.firebase.FirebaseSignaling
import co.netguru.chatroulette.data.model.IceCandidateFirebase
import co.netguru.chatroulette.feature.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import timber.log.Timber
import javax.inject.Inject


class VideoFragmentPresenter @Inject constructor(private val firebaseSignaling: FirebaseSignaling,
                                                 private val firebaseIceHandlers: FirebaseIceHandlers,
                                                 private val firebaseIceServers: FirebaseIceServers) : BasePresenter<VideoFragmentView>() {
    val disposables = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        disposables.dispose()
    }

    fun loadIceServers() {
        disposables += firebaseIceServers.getIceServers()
                .subscribeBy(
                        onSuccess = {
                            mvpView?.addIceServers(it)
                        },
                        onError = {
                            mvpView?.showServersRetrievingError()
                        }
                )
    }

    fun sendIceCandidates(iceCandidate: IceCandidate) {
        disposables += firebaseIceHandlers.sendIceCandidate(IceCandidateFirebase.createFromIceCandidate(iceCandidate))
                .compose(applyCompletableIoSchedulers())
                .subscribeBy(
                        onError = {
                            Timber.e(it, "Error while sending message")
                        },
                        onComplete = {
                            Timber.d("Ice message sent")
                        }
                )
    }

    fun removeIceCandidates(iceCandidates: Array<IceCandidate>) {
        disposables += firebaseIceHandlers.removeIceCandidates(IceCandidateFirebase.createFromIceCandidates(iceCandidates))
                .compose(applyCompletableIoSchedulers())
                .subscribeBy(
                        onComplete = {
                            Timber.d("Ice candidates successfully removed")
                        },
                        onError = {
                            Timber.d("Error while removing ice candidates $it")
                        }
                )
    }

    fun listenForIceCandidates(remoteUuid: String) {
        disposables += firebaseIceHandlers.getIceCandidates(remoteUuid)
                .compose(applyFlowableIoSchedulers())
                .subscribeBy(
                        onNext = {
                            Timber.d("Next ice: $it")
                            if (it is ChildEventAdded) {
                                mvpView?.addIceCandidate(it.data)
                            } else {
                                mvpView?.removeIceCandidate(it.data)
                            }
                        },
                        onError = {
                            Timber.e(it, "Error while listening for signals")
                        }
                )
    }

    fun sendOffer(deviceUuid: String, localDescription: SessionDescription) {
        disposables += firebaseSignaling.createOffer(deviceUuid, localDescription)
                .compose(applyCompletableIoSchedulers())
                .subscribeBy(
                        onComplete = {
                            Timber.d("description set")
                        },
                        onError = {
                            Timber.e(it, "Error occurred while setting description")
                        }
                )
    }

    fun listenForOffers() {
        disposables += firebaseSignaling.listenForOffers()
                .compose(applyFlowableIoSchedulers())
                .subscribeBy(
                        onNext = {
                            listenForIceCandidates(it.senderUuid)
                            mvpView?.handleRemoteDescription(it)
                        },
                        onError = {
                            Timber.e(it, "Error while listening for offers")
                        },
                        onComplete = {
                            //todo
                            Timber.d("Completed")
                        }

                )
    }

    fun sendAnswer(deviceUuid: String, localDescription: SessionDescription) {
        disposables += firebaseSignaling.createAnswer(deviceUuid, localDescription)
                .compose(applyCompletableIoSchedulers())
                .subscribeBy(
                        onComplete = {
                            Timber.d("sending answer completed")
                        },
                        onError = {
                            Timber.e(it, "Error occurred while sending answer")
                        }
                )
    }

    fun listenForAnswers() {
        disposables += firebaseSignaling.listenForAnswers()
                .compose(applyFlowableIoSchedulers())
                .subscribeBy(
                        onError = {
                            Timber.d("Error while listening for answers")
                        },
                        onNext = {
                            Timber.d("Next answer $it")
                            mvpView?.handleAnswer(it)
                        },
                        onComplete = {
                            Timber.d("Completed")
                        }
                )
    }

}
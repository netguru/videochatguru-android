package co.netguru.chatroulette.feature.main.video

import co.netguru.chatroulette.common.extension.ChildEventAdded
import co.netguru.chatroulette.common.util.RxUtils
import co.netguru.chatroulette.data.firebase.FirebaseIceCandidates
import co.netguru.chatroulette.data.firebase.FirebaseIceServers
import co.netguru.chatroulette.data.firebase.FirebaseSignalingAnswers
import co.netguru.chatroulette.data.firebase.FirebaseSignalingOffers
import co.netguru.chatroulette.data.model.IceCandidateFirebase
import co.netguru.chatroulette.feature.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import timber.log.Timber
import javax.inject.Inject


class VideoFragmentPresenter @Inject constructor(
        private val firebaseSignalingAnswers: FirebaseSignalingAnswers,
        private val firebaseSignalingOffers: FirebaseSignalingOffers,
        private val firebaseIceCandidates: FirebaseIceCandidates,
        private val firebaseIceServers: FirebaseIceServers) : BasePresenter<VideoFragmentView>() {

    private val disposables by lazy { CompositeDisposable() }

    private lateinit var remoteUuid: String

    override fun detachView() {
        super.detachView()
        disposables.dispose()
    }

    fun offerDevice(deviceUuid: String) {
        this.remoteUuid = deviceUuid
        listenForIceCandidates()
        mvpView?.createOffer()
    }

    fun loadIceServers() {
        disposables += firebaseIceServers.getIceServers()
                .subscribeBy(
                        onSuccess = {
                            listenForOffers()
                            mvpView?.addIceServers(it)
                        },
                        onError = {
                            mvpView?.showServersRetrievingError()
                        }
                )
    }

    fun sendIceCandidates(iceCandidate: IceCandidate) {
        disposables += firebaseIceCandidates.send(IceCandidateFirebase.createFromIceCandidate(iceCandidate))
                .compose(RxUtils.applyCompletableIoSchedulers())
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
        disposables += firebaseIceCandidates.remove(IceCandidateFirebase.createFromIceCandidates(iceCandidates))
                .compose(RxUtils.applyCompletableIoSchedulers())
                .subscribeBy(
                        onComplete = {
                            Timber.d("Ice candidates successfully removed")
                        },
                        onError = {
                            Timber.d("Error while removing ice candidates $it")
                        }
                )
    }

    fun listenForIceCandidates() {
        disposables += firebaseIceCandidates.get(remoteUuid)
                .compose(RxUtils.applyFlowableIoSchedulers())
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

    fun sendOffer(localDescription: SessionDescription) {
        disposables += firebaseSignalingOffers.create(remoteUuid, localDescription)
                .compose(RxUtils.applyCompletableIoSchedulers())
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
        disposables += firebaseSignalingOffers.listen()
                .compose(RxUtils.applyFlowableIoSchedulers())
                .subscribeBy(
                        onNext = {
                            val data = it.data
                            if (data != null) {
                                remoteUuid = it.data.senderUuid
                                listenForIceCandidates()
                                mvpView?.handleRemoteOffer(data)
                            } else {
                                //todo
                            }
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

    fun sendAnswer(localDescription: SessionDescription) {
        disposables += firebaseSignalingAnswers.create(remoteUuid, localDescription)
                .compose(RxUtils.applyCompletableIoSchedulers())
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
        disposables += firebaseSignalingAnswers.listen()
                .compose(RxUtils.applyFlowableIoSchedulers())
                .subscribeBy(
                        onError = {
                            Timber.e(it, "Error while listening for answers")
                        },
                        onNext = {
                            Timber.d("Next answer $it")
                            val data = it.data
                            if (data != null) {
                                mvpView?.handleRemoteAnswer(data)
                            } else {
                                //todo
                            }
                        },
                        onComplete = {
                            Timber.d("Completed")
                        }
                )
    }

}
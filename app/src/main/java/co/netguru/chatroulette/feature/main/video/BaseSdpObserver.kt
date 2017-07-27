package co.netguru.chatroulette.feature.main.video

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import timber.log.Timber


abstract class BaseSdpObserver : SdpObserver {

    override fun onSetFailure(p0: String) {
        Timber.d("onSetFailure() called with $p0")
    }

    override fun onSetSuccess() {
        Timber.d("onSetSuccess()")
    }

    override fun onCreateSuccess(p0: SessionDescription) {
        Timber.d("onCreateSuccess() called with $p0")
    }

    override fun onCreateFailure(p0: String) {
        Timber.d("onCreateFailure() called with $p0")
    }

}
package co.netguru.chatroulette.feature.main.video

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import timber.log.Timber


abstract class BaseSdpObserver : SdpObserver {

    override fun onSetFailure(error: String) {
        Timber.d("onSetFailure() called with $error")
    }

    override fun onSetSuccess() {
        Timber.d("onSetSuccess()")
    }

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        Timber.d("onCreateSuccess() called with $sessionDescription")
    }

    override fun onCreateFailure(error: String) {
        Timber.d("onCreateFailure() called with $error")
    }

}

abstract class SdpSetObserver {

    abstract fun onSetFailure(error: String)

    abstract fun onSetSuccess()

    fun toSdpObserver() = object : SdpObserver {
        override fun onSetFailure(error: String) {
            this@SdpSetObserver.onSetFailure(error)
        }

        override fun onSetSuccess() {
            this@SdpSetObserver.onSetSuccess()
        }

        override fun onCreateSuccess(sessionDescription: SessionDescription?) {
            throw IllegalStateException("onCreateSuccess called in set listener")
        }

        override fun onCreateFailure(error: String?) {
            throw IllegalStateException("onCreateFailure called in set listener")
        }

    }
}

abstract class SdpCreateObserver {

    abstract fun onCreateSuccess(sessionDescription: SessionDescription)

    abstract fun onCreateFailure(error: String)

    fun toSdpObserver() = object : SdpObserver {
        override fun onSetFailure(error: String) {
            throw IllegalStateException("onSetFailure called in set listener")
        }

        override fun onSetSuccess() {
            throw IllegalStateException("onSetSuccess called in set listener")
        }

        override fun onCreateSuccess(sessionDescription: SessionDescription) {
            this@SdpCreateObserver.onCreateSuccess(sessionDescription)
        }

        override fun onCreateFailure(error: String) {
            this@SdpCreateObserver.onCreateFailure(error)
        }

    }
}
package co.netguru.simplewebrtc.util

import android.util.Log


class Logger private constructor() {

    init {
        throw AssertionError()
    }

    companion object {

        private const val PREFIX = "WebRTC:"

        var loggingEnabled = false

        fun d(tag: String, message: String) {
            if (loggingEnabled) Log.d("$PREFIX $tag", message)
        }

        fun e(tag: String, message: String) {
            if (loggingEnabled) Log.e("$PREFIX $tag", message)
        }
    }
}
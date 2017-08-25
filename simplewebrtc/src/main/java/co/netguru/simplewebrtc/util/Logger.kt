package co.netguru.simplewebrtc.util

import android.util.Log


class Logger private constructor() {

    init {
        throw AssertionError()
    }

    companion object {

        var loggingEnabled = false

        fun d(tag: String, message: String) {
            if (loggingEnabled) {
                Log.d(tag, message)
            }
        }

        fun e(tag: String, message: String) {
            if (loggingEnabled) {
                Log.e(tag, message)
            }
        }
    }
}
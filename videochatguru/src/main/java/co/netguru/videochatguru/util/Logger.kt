package co.netguru.videochatguru.util

import android.util.Log


internal object Logger {
    private const val PREFIX = "WebRTC:"

    var loggingEnabled = false

    fun d(tag: String, message: String) {
        if (loggingEnabled) Log.d("$PREFIX $tag", message)
    }

    fun e(tag: String, message: String) {
        if (loggingEnabled) Log.e("$PREFIX $tag", message)
    }
}
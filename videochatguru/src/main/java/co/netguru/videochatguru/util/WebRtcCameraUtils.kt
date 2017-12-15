package co.netguru.videochatguru.util

import android.content.Context
import org.webrtc.Camera2Enumerator

internal object WebRtcCameraUtils {

    private var isCamera2Supported: Boolean? = null

    internal fun isCamera2Supported(context: Context): Boolean {
        if (isCamera2Supported == null) {
            isCamera2Supported = Camera2Enumerator.isSupported(context)
        }

        return isCamera2Supported!!
    }
}
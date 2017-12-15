package co.netguru.videochatguru.util

import android.annotation.SuppressLint
import android.content.Context
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.CameraVideoCapturer

internal object WebRtcUtils {

    //Suppressed as WebRtcCameraUtils.isCamera2Supported does expected platform checks
    @SuppressLint("NewApi")
    internal fun createCameraCapturerWithFrontAsDefault(context: Context) = createCameraCapturerWithFrontAsDefault(
            if (WebRtcCameraUtils.isCamera2Supported(context)) Camera2Enumerator(context) else Camera1Enumerator()
    )

    private fun createCameraCapturerWithFrontAsDefault(enumerator: CameraEnumerator): CameraVideoCapturer? {
        val (frontFacingCameras, backFacingAndOtherCameras) = enumerator.deviceNames
                .partition { enumerator.isFrontFacing(it) }

        return (frontFacingCameras.firstOrNull() ?: backFacingAndOtherCameras.firstOrNull())?.let {
            enumerator.createCapturer(it, null)
        }
    }
}

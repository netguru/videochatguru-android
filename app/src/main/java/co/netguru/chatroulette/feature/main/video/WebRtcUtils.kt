package co.netguru.chatroulette.feature.main.video

import android.content.Context
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.VideoCapturer

internal object WebRtcUtils {
    internal fun createFrontCameraCapturer(context: Context) = createFrontCameraCapturer(
            if (WebRtcCameraUtils.isCamera2Supported(context)) Camera2Enumerator(context) else Camera1Enumerator()
    )

    internal fun createBackCameraCapturer(context: Context) = createBackCameraCapturer(
            if (WebRtcCameraUtils.isCamera2Supported(context)) Camera2Enumerator(context) else Camera1Enumerator()
    )

    private fun createFrontCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        return enumerator.deviceNames
                .filter { enumerator.isFrontFacing(it) }
                .map { enumerator.createCapturer(it, null) }
                .firstOrNull()
    }

    private fun createBackCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        return enumerator.deviceNames
                .filter { enumerator.isBackFacing(it) }
                .map { enumerator.createCapturer(it, null) }
                .firstOrNull()
    }
}

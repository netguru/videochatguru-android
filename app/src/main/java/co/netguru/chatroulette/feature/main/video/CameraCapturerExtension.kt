package co.netguru.chatroulette.feature.main.video

import android.content.Context
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraCapturer
import org.webrtc.CameraVideoCapturer

fun CameraCapturer.createPlatformDependantFrontCameraCapturer(context: Context): CameraVideoCapturer? {
    val enumerator = if (Camera2Enumerator.isSupported(context)) Camera2Enumerator(context) else Camera1Enumerator()
    val (frontFacingDevices, backFacingDevices) = enumerator.deviceNames.partition {
        enumerator.isFrontFacing(it)
    }

    frontFacingDevices.mapNotNull { enumerator.createCapturer(it, null) }
            .forEach { return it }

    return backFacingDevices.map { enumerator.createCapturer(it, null) }
            .firstOrNull { it != null }
}
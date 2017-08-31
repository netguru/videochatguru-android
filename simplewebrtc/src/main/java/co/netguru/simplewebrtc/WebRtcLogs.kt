package co.netguru.simplewebrtc

import co.netguru.simplewebrtc.util.Logger
import org.webrtc.Logging


object WebRtcLogs {

    /**
     * By default WebRtc implementation logs internal information, all logs can be blocked using this method call
     */
    fun disableLogs() {
        Logging.enableLogToDebugOutput(Logging.Severity.LS_NONE)
        Logger.loggingEnabled = false
    }

    /**
     * Enable additional logging - this might be helpful while resolving problems and should be used only in debug builds.
     */
    fun enableSimpleWebRtcLogs(enabled: Boolean) {
        Logger.loggingEnabled = enabled
    }

    /**
     * Enable webrtc internal logging - this might be helpful while resolving problems and should be used only in debug builds.
     */
    fun enableInternalLogs(severity: Logging.Severity) {
        Logging.enableLogToDebugOutput(severity)
    }
}
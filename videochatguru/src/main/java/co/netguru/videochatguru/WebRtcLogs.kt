package co.netguru.videochatguru

import co.netguru.videochatguru.util.Logger
import org.webrtc.Logging

/**
 * Enable or disable additional logging - this might be helpful while resolving problems and should be enabled only in debug builds.
 */
fun enableWebRtcLogs(logsEnabled: Boolean) {
    Logger.loggingEnabled = logsEnabled
}

/**
 * Enable webrtc internal logging - this might be helpful while resolving problems and should be used only in debug builds.
 */
fun enableInternalWebRtclogs(severity: Logging.Severity) {
    Logging.enableLogToDebugOutput(severity)
}

/**
 * By default WebRtc implementation logs internal information, all logs can be blocked using this method call
 */
fun disableWebRtcLogs() {
    Logging.enableLogToDebugOutput(Logging.Severity.LS_NONE)
    Logger.loggingEnabled = false
}
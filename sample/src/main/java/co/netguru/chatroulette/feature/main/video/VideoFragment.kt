package co.netguru.chatroulette.feature.main.video

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.View
import co.netguru.chatroulette.R
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.feature.base.BaseMvpFragment
import co.netguru.chatroulette.webrtc.service.WebRtcService
import co.netguru.chatroulette.webrtc.service.WebRtcServiceListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_video.*
import org.webrtc.PeerConnection
import timber.log.Timber


class VideoFragment : BaseMvpFragment<VideoFragmentView, VideoFragmentPresenter>(), VideoFragmentView, WebRtcServiceListener {

    companion object {
        val TAG: String = VideoFragment::class.java.name

        fun newInstance() = VideoFragment()

        private const val KEY_IN_CHAT = "key:in_chat"
    }

    private lateinit var serviceConnection: ServiceConnection

    override fun getLayoutId() = R.layout.fragment_video

    override fun retrievePresenter() = App.getApplicationComponent(context).videoFragmentComponent().videoFragmentPresenter()

    var service: WebRtcService? = null

    override val remoteUuid
        get() = service?.getRemoteUuid()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (buttonPanel.layoutParams as CoordinatorLayout.LayoutParams).behavior = MoveUpBehavior()
        (localVideoView.layoutParams as CoordinatorLayout.LayoutParams).behavior = MoveUpBehavior()
        activity.volumeControlStream = AudioManager.STREAM_VOICE_CALL

        if (savedInstanceState?.getBoolean(KEY_IN_CHAT) == true) {
            initAlreadyRunningConnection()
        }
        connectButton.setOnClickListener {
            checkPermissionsAndConnect()
        }

        disconnectButton.setOnClickListener {
            getPresenter().disconnectByUser()
        }

        switchCameraButton.setOnClickListener {
            service?.switchCamera()
        }

        cameraEnabledToggle.setOnCheckedChangeListener { _, enabled ->
            service?.enableCamera(enabled)
        }

        microphoneEnabledToggle.setOnCheckedChangeListener { _, enabled ->
            service?.enableMicrophone(enabled)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        service?.let {
            it.detachViews()
            unbindService()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (remoteVideoView.visibility == View.VISIBLE) {
            outState.putBoolean(KEY_IN_CHAT, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!activity.isChangingConfigurations) disconnect()
    }

    override fun attachService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                onWebRtcServiceConnected((iBinder as (WebRtcService.LocalBinder)).service)
                getPresenter().startRoulette()
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                onWebRtcServiceDisconnected()
            }
        }
        startAndBindWebRTCService(serviceConnection)
    }

    override fun criticalWebRTCServiceException(throwable: Throwable) {
        unbindService()
        showSnackbarMessage(R.string.error_web_rtc_error, Snackbar.LENGTH_LONG)
        Timber.e(throwable, "Critical WebRTC service error")
    }

    override fun connectionStateChange(iceConnectionState: PeerConnection.IceConnectionState) {
        getPresenter().connectionStateChange(iceConnectionState)
    }

    override fun connectTo(uuid: String) {
        service?.offerDevice(uuid)
    }

    override fun disconnect() {
        service?.let {
            it.stopSelf()
            unbindService()
        }
    }

    private fun unbindService() {
        service?.let {
            it.detachServiceActionsListener()
            context.unbindService(serviceConnection)
            service = null
        }
    }

    override fun showCamViews() {
        buttonPanel.visibility = View.VISIBLE
        remoteVideoView.visibility = View.VISIBLE
        localVideoView.visibility = View.VISIBLE
        connectButton.visibility = View.GONE
    }

    override fun showStartRouletteView() {
        buttonPanel.visibility = View.GONE
        remoteVideoView.visibility = View.GONE
        localVideoView.visibility = View.GONE
        connectButton.visibility = View.VISIBLE
    }

    override fun showErrorWhileChoosingRandom() {
        showSnackbarMessage(R.string.error_choosing_random_partner, Snackbar.LENGTH_LONG)
    }

    override fun showNoOneAvailable() {
        showSnackbarMessage(R.string.msg_no_one_available, Snackbar.LENGTH_LONG)
    }

    override fun showLookingForPartnerMessage() {
        showSnackbarMessage(R.string.msg_looking_for_partner, Snackbar.LENGTH_SHORT)
    }

    override fun showOtherPartyFinished() {
        showSnackbarMessage(R.string.msg_other_party_finished, Snackbar.LENGTH_SHORT)
    }

    override fun showConnectedMsg() {
        showSnackbarMessage(R.string.msg_connected_to_other_party, Snackbar.LENGTH_LONG)
    }

    override fun showWillTryToRestartMsg() {
        showSnackbarMessage(R.string.msg_will_try_to_restart_msg, Snackbar.LENGTH_LONG)
    }

    private fun initAlreadyRunningConnection() {
        showCamViews()
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                onWebRtcServiceConnected((iBinder as (WebRtcService.LocalBinder)).service)
                getPresenter().listenForDisconnectOrders()
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                onWebRtcServiceDisconnected()
            }
        }
        startAndBindWebRTCService(serviceConnection)
    }

    private fun startAndBindWebRTCService(serviceConnection: ServiceConnection) {
        WebRtcService.startService(context)
        WebRtcService.bindService(context, serviceConnection)
    }

    private fun checkPermissionsAndConnect() {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .withListener(CompositeMultiplePermissionsListener(
                        object : BaseMultiplePermissionsListener() {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) getPresenter().connect()
                            }
                        },
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                                .with(coordinatorLayout, R.string.msg_permissions)
                                .withOpenSettingsButton(R.string.action_settings)
                                .withDuration(Snackbar.LENGTH_LONG)
                                .build())
                ).check()
    }

    private fun onWebRtcServiceConnected(service: WebRtcService) {
        Timber.d("Service connected")
        this.service = service
        service.attachLocalView(localVideoView)
        service.attachRemoteView(remoteVideoView)
        syncButtonsState(service)
        service.attachServiceActionsListener(webRtcServiceListener = this)
    }

    private fun syncButtonsState(service: WebRtcService) {
        cameraEnabledToggle.isChecked = service.isCameraEnabled()
        microphoneEnabledToggle.isChecked = service.isMicrophoneEnabled()
    }

    private fun onWebRtcServiceDisconnected() {
        Timber.d("Service disconnected")
    }
}
package co.netguru.chatroulette.feature.main.video

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import co.netguru.chatroulette.R
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.feature.base.BaseMvpFragment
import co.netguru.chatroulette.webrtc.service.WebRtcService
import kotlinx.android.synthetic.main.fragment_video.*
import timber.log.Timber


class VideoFragment : BaseMvpFragment<VideoFragmentView, VideoFragmentPresenter>(), VideoFragmentView, ServiceConnection {


    companion object {
        val TAG: String = VideoFragment::class.java.name

        fun newInstance() = VideoFragment()
    }

    override fun getLayoutId() = R.layout.fragment_video

    override fun retrievePresenter() = App.getApplicationComponent(context).videoFragmentComponent().videoFragmentPresenter()

    lateinit var service: WebRtcService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.volumeControlStream = AudioManager.STREAM_VOICE_CALL
        connectButton.setOnClickListener {
            getPresenter().connect()
        }
        disconnectButton.setOnClickListener {
            getPresenter().disconnect()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        service.detachViews()
        context.applicationContext.unbindService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!activity.isChangingConfigurations) {
            service.stopSelf()
        }
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        Timber.d("Service disconnected")
    }

    override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
        Timber.d("Service connected")
        service = (iBinder as (WebRtcService.LocalBinder)).service
        service.attachLocalView(localVideoView)
        service.attachRemoteView(remoteVideoView)
        getPresenter().startRoulette()

    }

    override fun connectTo(uuid: String) {
        service.offerDevice(uuid)
    }

    override fun attachService() {
        val intent = Intent(activity, WebRtcService::class.java)
        context.startService(intent)
        context.applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun disconnect() {
        context.applicationContext.unbindService(this)
        service.stopSelf()
    }

    override fun showCamViews() {
        remoteVideoView.visibility = View.VISIBLE
        localVideoView.visibility = View.VISIBLE
        buttonPanel.visibility = View.VISIBLE
        connectButton.visibility = View.GONE
    }

    override fun showStartRouletteView() {
        remoteVideoView.visibility = View.GONE
        localVideoView.visibility = View.GONE
        buttonPanel.visibility = View.GONE
        connectButton.visibility = View.VISIBLE
    }
}
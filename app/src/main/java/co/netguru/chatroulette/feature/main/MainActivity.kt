package co.netguru.chatroulette.feature.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import co.netguru.chatroulette.R
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.feature.base.BaseMvpActivity
import co.netguru.chatroulette.feature.main.video.VideoFragment
import co.netguru.chatroulette.feature.main.video.VideoFragmentOld
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {

    val videoFragment = VideoFragment.newInstance()
    //val videoFragment = VideoFragmentOld.newInstance()


    override fun retrievePresenter() = App.getApplicationComponent(this).mainComponent().mainPresenter()

    override fun getLayoutId() = R.layout.activity_main

    fun Context.startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thisDeviceUuidText.text = App.CURRENT_DEVICE_UUID
        connectButton.setOnClickListener { connectToDevice() }
        disconnectButton.setOnClickListener { disconnectDevice() }
        getReplaceFragmentTransaction(R.id.fragmentContainer, videoFragment, VideoFragment.TAG).commit()
    }

    private fun connectToDevice() {
        getPresenter().startSearching()
    }

    private fun disconnectDevice() {
        getPresenter().stop()
    }

    override fun passOfferDevice(deviceUuid: String) {
        //TODO REMOVE!
        Handler().postDelayed({ videoFragment.connectToDevice(deviceUuid) }, 2000)

    }
}
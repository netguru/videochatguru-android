package co.netguru.chatroulette.feature.main

import android.os.Bundle
import co.netguru.chatroulette.R
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.feature.base.BaseMvpActivity
import co.netguru.chatroulette.feature.main.video.VideoFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {

    val videoFragment = VideoFragment.newInstance()

    override fun retrievePresenter() = App.getApplicationComponent(this).mainComponent().mainPresenter()

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thisDeviceUuidText.text = App.CURRENT_DEVICE_UUID
        if (savedInstanceState == null) {
            getReplaceFragmentTransaction(R.id.fragmentContainer, videoFragment, VideoFragment.TAG).commit()
        }
    }

}
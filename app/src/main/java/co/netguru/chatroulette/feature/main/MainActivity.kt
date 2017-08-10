package co.netguru.chatroulette.feature.main

import android.os.Bundle
import android.widget.Toast
import co.netguru.chatroulette.BuildConfig
import co.netguru.chatroulette.R
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.feature.base.BaseMvpActivity
import co.netguru.chatroulette.feature.main.video.VideoFragment


class MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {

    val videoFragment = VideoFragment.newInstance()

    override fun retrievePresenter() = App.getApplicationComponent(this).mainComponent().mainPresenter()

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            getReplaceFragmentTransaction(R.id.fragmentContainer, videoFragment, VideoFragment.TAG).commit()
        }
    }

}
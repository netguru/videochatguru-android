package co.netguru.chatroulette.feature.main

import android.os.Bundle
import co.netguru.chatroulette.R
import co.netguru.chatroulette.feature.base.BaseActivity
import co.netguru.chatroulette.feature.main.video.VideoFragment


class MainActivity : BaseActivity() {

    private val videoFragment = VideoFragment.newInstance()

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            getReplaceFragmentTransaction(R.id.fragmentContainer, videoFragment, VideoFragment.TAG).commit()
        }
    }
}
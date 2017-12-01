package co.netguru.android.chatandroll.feature.main

import android.os.Bundle
import co.netguru.android.chatandroll.R
import co.netguru.android.chatandroll.feature.base.BaseActivity
import co.netguru.android.chatandroll.feature.main.video.VideoFragment


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
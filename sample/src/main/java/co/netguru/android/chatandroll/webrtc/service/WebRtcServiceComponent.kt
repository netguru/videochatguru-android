package co.netguru.android.chatandroll.webrtc.service

import co.netguru.android.chatandroll.common.di.ServiceScope
import dagger.Subcomponent

@ServiceScope
@Subcomponent
interface WebRtcServiceComponent {

    fun inject(webRtcService: WebRtcService)

}
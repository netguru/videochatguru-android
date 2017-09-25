package co.netguru.chatroulette.webrtc.service

import co.netguru.chatroulette.common.di.ServiceScope
import dagger.Subcomponent

@ServiceScope
@Subcomponent
interface WebRtcServiceComponent {

    fun inject(webRtcService: WebRtcService)

}
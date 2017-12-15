package co.netguru.android.chatandroll.app

import android.app.Application
import android.content.Context
import android.content.res.Resources
import co.netguru.android.chatandroll.data.firebase.FirebaseIceCandidates
import co.netguru.android.chatandroll.data.firebase.FirebaseIceServers
import co.netguru.android.chatandroll.data.firebase.FirebaseSignalingAnswers
import co.netguru.android.chatandroll.data.firebase.FirebaseSignalingOffers
import co.netguru.android.chatandroll.webrtc.service.WebRtcServiceController
import co.netguru.videochatguru.WebRtcClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideApplication() = application

    @Provides
    @Singleton
    fun provideResources(): Resources = application.resources

    @Provides
    fun provideWebRtcClient(context: Context) = WebRtcClient(context)

    @Provides
    fun provideWebRtcServiceController(webRtcClient: WebRtcClient, firebaseSignalingAnswers: FirebaseSignalingAnswers,
                                       firebaseSignalingOffers: FirebaseSignalingOffers, firebaseIceCandidates: FirebaseIceCandidates,
                                       firebaseIceServers: FirebaseIceServers): WebRtcServiceController {
        return WebRtcServiceController(
                webRtcClient, firebaseSignalingAnswers, firebaseSignalingOffers,
                firebaseIceCandidates, firebaseIceServers)
    }
}
package co.netguru.chatroulette.feature.main.video

import android.os.Bundle
import android.view.View
import co.netguru.chatroulette.R
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.data.model.SessionDescriptionFirebase
import co.netguru.chatroulette.feature.base.BaseMvpFragment
import kotlinx.android.synthetic.main.fragment_video.*
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription


class VideoFragment : BaseMvpFragment<VideoFragmentView, VideoFragmentPresenter>(), VideoFragmentView {

    val webRtcClient by lazy { WebRtcClient(context) }

    companion object {
        val TAG: String = VideoFragment::class.java.name

        fun newInstance() = VideoFragment()
    }

    override fun getLayoutId() = R.layout.fragment_video

    override fun retrievePresenter() = App.getApplicationComponent(context).videoFragmentComponent().videoFragmentPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webRtcClient.attachLocalView(localVideoView)
        webRtcClient.attachRemoteView(remoteVideoView)
        //todo remove
        getPresenter().loadIceServers()
        //todo permissions
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webRtcClient.dispose()
    }

    fun connectToDevice(deviceUuid: String) {
        getPresenter().offerDevice(deviceUuid)
    }

    override fun handleRemoteOffer(remoteSessionDescription: SessionDescriptionFirebase) {
        //todo parse in presenter
        webRtcClient.handleRemoteOffer(remoteSessionDescription.toSessionDescription())
    }

    override fun handleRemoteAnswer(answer: SessionDescriptionFirebase) {
        webRtcClient.handleRemoteAnswer(answer.toSessionDescription())
    }

    override fun addIceCandidate(iceCandidate: IceCandidate) {
        webRtcClient.addIceCandidate(iceCandidate)
    }

    override fun removeIceCandidate(iceCandidate: IceCandidate) {
        webRtcClient.removeIceCandidate(iceCandidate)
    }

    override fun addIceServers(iceServers: List<PeerConnection.IceServer>) {
        webRtcClient.initialize(iceServers, object : PeerConnectionListener {

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                getPresenter().sendIceCandidates(iceCandidate)
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                getPresenter().removeIceCandidates(iceCandidates)
            }

            override fun onOffer(sessionDescription: SessionDescription) {
                getPresenter().sendOffer(sessionDescription)
                getPresenter().listenForAnswers()
            }

            override fun onAnswer(sessionDescription: SessionDescription) {
                getPresenter().sendAnswer(sessionDescription)
            }

        })
    }

    override fun showServersRetrievingError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createOffer() {
        webRtcClient.createOffer()
    }

}


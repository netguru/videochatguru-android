package co.netguru.chatroulette.feature.main.video

import android.os.Bundle
import android.view.View
import co.netguru.chatroulette.R
import co.netguru.chatroulette.app.App
import co.netguru.chatroulette.data.model.SessionDescriptionFirebase
import co.netguru.chatroulette.feature.base.BaseMvpFragment
import kotlinx.android.synthetic.main.fragment_video.*
import org.webrtc.*
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils
import timber.log.Timber


class VideoFragment : BaseMvpFragment<VideoFragmentView, VideoFragmentPresenter>(), VideoFragmentView {

    lateinit var eglBase: EglBase
    lateinit var peer: PeerConnection

    companion object {
        val TAG: String = VideoFragment::class.java.name

        fun newInstance() = VideoFragment()
    }

    private lateinit var localAudioTrack: AudioTrack

    private lateinit var localVideoTrack: VideoTrack

    private lateinit var sdpConstraints: MediaConstraints

    private lateinit var factory: PeerConnectionFactory

    override fun getLayoutId() = R.layout.fragment_video

    override fun retrievePresenter() = App.getApplicationComponent(context).videoFragmentComponent().videoFragmentPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo Create webrtc client that will handle setup
        //PeerConnectionFactory.initializeInternalTracer()
        //PeerConnectionFactory.initializeFieldTrials("")
        //todo check on device with support
        WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false)
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false)
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(false)//deprecated
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false)

        if (!PeerConnectionFactory.initializeAndroidGlobals(context, true, true, false)) {
            Timber.d("Failed to initializeAndroidGlobals")
        }

        factory = PeerConnectionFactory(PeerConnectionFactory.Options())

        val videoCapturer = WebRtcUtils.createFrontCameraCapturer(context)

        val mediaConstraints = MediaConstraints()
        eglBase = EglBase.create()
        factory.setVideoHwAccelerationOptions(eglBase.eglBaseContext, eglBase.eglBaseContext)
        initVideoViews()
        val videoSource = factory.createVideoSource(videoCapturer)
        localVideoTrack = factory.createVideoTrack("100", videoSource)
        videoCapturer?.startCapture(1280, 720, 30)
        //todo check if really needed
        localVideoTrack.addRenderer(VideoRenderer(localVideoView))

        val audioSource = factory.createAudioSource(mediaConstraints)
        localAudioTrack = factory.createAudioTrack("101", audioSource)

        sdpConstraints = MediaConstraints()
        sdpConstraints.optional.add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))

        getPresenter().loadIceServers()
        //todo permissions
        //width,height, fps
    }

    private fun initVideoViews() {
        localVideoView.init(eglBase.eglBaseContext, null)
        localVideoView.setEnableHardwareScaler(true)

        remoteVideoView.init(eglBase.eglBaseContext, null)
        remoteVideoView.setEnableHardwareScaler(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disconnect()
        eglBase.release()
    }

    fun disconnect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun offerDevice(deviceUuid: String) {
        getPresenter().listenForIceCandidates(deviceUuid)
        peer.createOffer(object : SdpObserver {

            lateinit var localDescription: SessionDescription

            override fun onSetFailure(error: String) {
                Timber.e("onSetError() $error")
            }

            override fun onSetSuccess() {
                Timber.d("onSetSuccess()")
                //here we should pass info about description to other party
                getPresenter().sendOffer(deviceUuid, localDescription)
            }

            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                localDescription = sessionDescription
                peer.setLocalDescription(this, sessionDescription)
            }

            override fun onCreateFailure(error: String) {
                Timber.e("onCreateFailure() with $error")
            }

        }, sdpConstraints)
    }

    override fun handleRemoteDescription(remoteSessionDescription: SessionDescriptionFirebase) {
        peer.setRemoteDescription(object : SdpObserver {
            override fun onSetFailure(error: String) {
                Timber.e("onSetError() $error")
            }

            override fun onSetSuccess() {
                peer.createAnswer(this, sdpConstraints)
            }

            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peer.setLocalDescription(object : SdpObserver {
                    override fun onSetFailure(error: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onSetSuccess() {
                        getPresenter().sendAnswer(remoteSessionDescription.senderUuid, sessionDescription)
                    }

                    override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onCreateFailure(error: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }, sessionDescription)
            }

            override fun onCreateFailure(error: String) {
                Timber.e("onCreateFailure() with $error")
            }

        }, remoteSessionDescription.toSessionDescription())
    }


    override fun handleAnswer(answer: SessionDescriptionFirebase) {
        //todo beautify sdp observer
        peer.setRemoteDescription(object : SdpSetObserver() {
            override fun onSetFailure(error: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSetSuccess() {
                Timber.d("setRemoteDescription from answer success")
            }
        }.toSdpObserver(), answer.toSessionDescription())
    }

    override fun addIceCandidate(data: IceCandidate) {
        peer.addIceCandidate(data)
    }

    override fun removeIceCandidate(data: IceCandidate) {
        //todo
        peer.removeIceCandidates(arrayOf(data))
    }

    override fun addIceServers(it: List<PeerConnection.IceServer>) {
        val rtcConfig = PeerConnection.RTCConfiguration(it)

        peer = factory.createPeerConnection(rtcConfig, sdpConstraints, object : CustomPeerConnectionObserver() {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                super.onIceCandidate(iceCandidate)
                getPresenter().sendIceCandidates(iceCandidate)
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                super.onIceCandidatesRemoved(iceCandidates)
                getPresenter().removeIceCandidates(iceCandidates)
            }

            override fun onAddStream(mediaStream: MediaStream) {
                super.onAddStream(mediaStream)
                //todo
                if (mediaStream.audioTracks.size > 1 || mediaStream.videoTracks.size > 1) {
                    Timber.e("Weird-looking stream: " + mediaStream)
                    return
                }
                if (mediaStream.videoTracks.size == 1) {
                    mediaStream.preservedVideoTracks
                    val remoteVideoTrack = mediaStream.videoTracks[0]
                    remoteVideoTrack.setEnabled(true)

                    remoteVideoTrack.addRenderer(VideoRenderer(remoteVideoView))
                }

            }
        })

        val stream = factory.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        peer.addStream(stream)

        getPresenter().listenForAnswers()
        getPresenter().listenForOffers()
    }

    override fun showServersRetrievingError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


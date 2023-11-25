package com.fpoly.smartlunch.ui.chat.call

import android.app.Application
import android.util.Log
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.network.SocketManager
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import javax.inject.Inject

class WebRTCClient @Inject constructor(
    private val application: Application,
) {
    private lateinit var observer: PeerConnection.Observer

    fun observerPeerConnection(observer: PeerConnection.Observer){
        this.observer = observer
    }


    init {
        initPeerConnectionFactory(application)
    }

    private val eglContext = EglBase.create()
    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private val iceServer = listOf(
        PeerConnection.IceServer.builder("stun:iphone-stun.strato-iphone.de:3478").createIceServer(),
        PeerConnection.IceServer("stun:openrelay.metered.ca:80"),
        PeerConnection.IceServer("turn:openrelay.metered.ca:80","openrelayproject","openrelayproject"),
        PeerConnection.IceServer("turn:openrelay.metered.ca:443","openrelayproject","openrelayproject"),
        PeerConnection.IceServer("turn:openrelay.metered.ca:443?transport=tcp","openrelayproject","openrelayproject"),
    )
    private val peerConnection by lazy { createPeerConnection(observer) }
    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }

    private var localStream: MediaStream? = null

    private var videoCapturer: CameraVideoCapturer? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null

    private fun initPeerConnectionFactory(application: Application) {
        val peerConnectionOption = PeerConnectionFactory.InitializationOptions.builder(application)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()

        PeerConnectionFactory.initialize(peerConnectionOption)
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(
                eglContext.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglContext.eglBaseContext))
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = true
                disableNetworkMonitor = true
            }).createPeerConnectionFactory()
    }

    private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        return peerConnectionFactory.createPeerConnection(iceServer, observer)
    }

    fun initializeSurfaceView(surface: SurfaceViewRenderer) {
        surface.run {
            setEnableHardwareScaler(true)
            setMirror(true)
            init(eglContext.eglBaseContext, null)
        }
    }

    fun startLocalVideo(surface: SurfaceViewRenderer) {
        val surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().name, eglContext.eglBaseContext)
        videoCapturer = getVideoCapturer(application)
        videoCapturer?.initialize(surfaceTextureHelper, surface.context,
            localVideoSource.capturerObserver)
        videoCapturer?.startCapture(1920, 1080, 120)

        localVideoTrack = peerConnectionFactory.createVideoTrack("local_track", localVideoSource)
        localVideoTrack?.addSink(surface)
        localAudioTrack = peerConnectionFactory.createAudioTrack("local_track_audio", localAudioSource)

        localStream = peerConnectionFactory.createLocalMediaStream("local_stream")
        localStream!!.addTrack(localAudioTrack)
        localStream!!.addTrack(localVideoTrack)
        peerConnection?.addStream(localStream)
    }

    private fun getVideoCapturer(application: Application): CameraVideoCapturer {
        return Camera2Enumerator(application).run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it, null)
            } ?: throw
            IllegalStateException()
        }
    }
    fun call(myUser: User, target: User, resultToSocket : (reqCall: RequireCall) -> Unit) {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))

        peerConnection?.createOffer(object : SdpObserver{
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection?.setLocalDescription(object: SdpObserver{
                    override fun onCreateSuccess(p0: SessionDescription?) {
                    }
                    override fun onSetSuccess() {
                        val offer = hashMapOf(
                            "sdp" to desc?.description,                                             // sdp: chứa thông tin của media
                            "type" to desc?.type
                        )
                        resultToSocket(RequireCall(RequireCallType.CREATE_OFFER, myUser, target, offer))
                    }

                    override fun onCreateFailure(p0: String?) {
                    }
                    override fun onSetFailure(p0: String?) {
                    }
                }, desc)
            }

            override fun onSetSuccess() {
            }

            override fun onCreateFailure(p0: String?) {
            }
            override fun onSetFailure(p0: String?) {
            }
        }, mediaConstraints)
    }

    fun onRemoteSessionReceived(session: SessionDescription) {
        peerConnection?.setRemoteDescription(object: SdpObserver{
            override fun onCreateSuccess(p0: SessionDescription?) {

            }

            override fun onSetSuccess() {
            }

            override fun onCreateFailure(p0: String?) {
            }

            override fun onSetFailure(p0: String?) {
            }

        }, session)
    }

    fun answer(myUser: User, target: User, resultToSocket : (reqCall: RequireCall) -> Unit) {
        val constraints = MediaConstraints()
        constraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))

        peerConnection?.createAnswer(object: SdpObserver{
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection?.setLocalDescription(object: SdpObserver{
                    override fun onCreateSuccess(p0: SessionDescription?) {
                    }
                    override fun onSetSuccess() {
                        val answer = hashMapOf(
                            "sdp" to desc?.description,
                            "type" to desc?.type
                        )
                        resultToSocket(RequireCall(RequireCallType.CREATE_ANSWER, myUser, target, answer))
                    }

                    override fun onCreateFailure(p0: String?) {
                    }

                    override fun onSetFailure(p0: String?) {
                    }

                }, desc)
            }

            override fun onSetSuccess() {
            }

            override fun onCreateFailure(p0: String?) {
            }

            override fun onSetFailure(p0: String?) {
            }
        }, constraints)
    }

    fun addIceCandidate(p0: IceCandidate?) {
        Log.d("WebRTCClient", "CALLCHAT IceCandidate: $p0", )
        peerConnection?.addIceCandidate(p0)
    }

    fun startCall(){
        localVideoTrack?.setEnabled(true)
        localAudioTrack?.setEnabled(true)

//        peerConnection?.connectionState()
    }
    fun endCall() {
        localVideoTrack?.setEnabled(false)
        localAudioTrack?.setEnabled(false)
        videoCapturer?.stopCapture()
//        peerConnection?.removeStream(localStream)
//        eglContext.release()
//        peerConnection?.close()
    }

    fun swichVideoCapture(){
        videoCapturer?.switchCamera(null)
    }

    fun toggleVideo(isCameraPause: Boolean) {
        localVideoTrack?.setEnabled(isCameraPause)
    }

    fun toggleAudio(isMute: Boolean) {
        localAudioTrack?.setEnabled(isMute)
    }

}
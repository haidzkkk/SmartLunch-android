package com.fpoly.smartlunch.ui.chat.room

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipboardManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.GalleryVideo
import com.fpoly.smartlunch.data.model.GalleyImage
import com.fpoly.smartlunch.data.model.Image
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.MessageType
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.DialogVideoBinding
import com.fpoly.smartlunch.databinding.FragmentRoomChatBinding
import com.fpoly.smartlunch.ui.call.CallActivity
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.fpoly.smartlunch.ultis.checkPermisionCamera
import com.fpoly.smartlunch.ultis.checkPermissionGallery
import com.fpoly.smartlunch.ultis.hideKeyboard
import com.fpoly.smartlunch.ultis.setMargins
import com.fpoly.smartlunch.ultis.showKeyboard
import com.fpoly.smartlunch.ultis.showSnackbar
import com.fpoly.smartlunch.ultis.startActivityWithData
import com.fpoly.smartlunch.ultis.startToDetailPermission
import com.fpoly.smartlunch.ultis.uriToFilePath
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.stfalcon.imageviewer.StfalconImageViewer


class RoomChatFragment : PolyBaseFragment<FragmentRoomChatBinding>() {

    private val displaySize = DisplayMetrics()
    private lateinit var bottomBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var adapter: RoomChatAdapter
    private var adapterGallery: GalleryBottomChatAdapter? = null
    private val chatViewmodel: ChatViewmodel by activityViewModel()

    private var listSelectGallery: ArrayList<Gallery> = arrayListOf()
    private var pathPhoto: String = ""

    private var takePictureLauncher: ActivityResultLauncher<Uri> = registerForActivityResult(
        ActivityResultContracts.TakePicture()
        ) { result ->
            try {
                if (result) {
                    handleSendMessageImages()
                }else{
                    Toast.makeText(requireContext(), getString(R.string.taking_photo_faild), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRoomChatBinding {
        return FragmentRoomChatBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setupRcv()
        setupBottomSheetBihavior()
        listenClickUi()
        setupViewModel()
        listenOnBackPress()
    }

    private fun initUI() {
//        chatViewmodel.initObserverPeerConnection()
        views.layoutHeader.imgBack.isVisible = true
    }

    private fun setupRcv() {
            adapter = RoomChatAdapter(object : RoomChatAdapter.IOnClickLisstenner {
                @SuppressLint("ClickableViewAccessibility", "ResourceType")
                override fun onClickItem(message: Message) {
                    handleBottomGallery(false)
                    context?.hideKeyboard(views.root)

                    if (message.type == MessageType.TYPE_IMAGE){
                        if (message.images.isNullOrEmpty()) return
                        showScreenPhoto(message.images)
                    }
                }

                override fun onLongClickItem(message: Message) {
                    val cm: ClipboardManager = requireContext().applicationContext.getSystemService(
                        Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.text = message.message.toString()
                }

                override fun onClickItemJoinCall(message: Message) {
                    val intent = Intent(requireContext(), CallActivity::class.java)
                    val targetUserId = withState(chatViewmodel){it.curentRoom.invoke()?.shopUserId?._id}
                    requireActivity().startActivityWithData(intent, MyConfigNotifi.TYPE_CALL_ANSWER, targetUserId)
                }

                override fun onClickItemCall() {
                    val idTargetUser: String? = withState(chatViewmodel){ it.curentRoom.invoke()?.shopUserId?._id}
                    val intent = Intent(requireContext(), CallActivity::class.java)
                    requireActivity().startActivityWithData(intent, MyConfigNotifi.TYPE_CALL_OFFER, idTargetUser)
                }
            })

            views.rcvChat.adapter = adapter
            views.rcvChat.recycledViewPool.clear()
            views.rcvChat.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupBottomSheetBihavior() {
        requireActivity().windowManager.defaultDisplay.getMetrics(displaySize)

        bottomBehavior = BottomSheetBehavior.from(views.layoutBehavior).apply {
            this.state = STATE_COLLAPSED
            this.isHideable = false
            this.peekHeight = (displaySize.heightPixels * 0.3).toInt()
        }

        adapterGallery =
            GalleryBottomChatAdapter(object : GalleryBottomChatAdapter.IOnClickLisstenner {
                override fun onClickSelectItem(gallery: Gallery) {
                    listSelectGallery.add(gallery)

                    views.btnSendImage.isVisible = listSelectGallery.size > 0
                }

                override fun onClickUnSelectItem(gallery: Gallery) {
                    listSelectGallery.remove(gallery)

                    views.btnSendImage.isVisible = listSelectGallery.size > 0
                }

                override fun onLongClickItem(gallery: Gallery) {
                    if (gallery is GalleyImage){
                        showScreenPhoto(arrayOf(gallery))
                    }else if(gallery is GalleryVideo){
                        showDialogVideo(gallery)
                    }
                    views.btnSendImage.isVisible = listSelectGallery.size > 0
                }
            })
        views.rcvGallery.adapter = adapterGallery
        views.rcvGallery.layoutManager = GridLayoutManager(requireContext(), 4)

        checkPermissionGallery {
            checkResutlPerGallery(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listenClickUi() {
        views.layoutHeader.imgBack.setOnClickListener{
            activity?.onBackPressed()
        }

        views.edtMessage.setOnFocusChangeListener { view, b ->
            if (b) handleBottomGallery(false)
        }

        views.edtMessage.setOnEditorActionListener{v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                views.imgSend.performClick()
            }
            true
        }

        views.imgSend.setOnClickListener {
            handleSendMessage()
        }

        views.btnSendImage.setOnClickListener {
            handleSendMessageImages()
            bottomBehavior.state = STATE_COLLAPSED
            context?.hideKeyboard(views.root)
        }

        views.imgGallery.setOnClickListener {
            handleBottomGallery(!views.layoutBehavior.isVisible)
            context?.hideKeyboard(views.root)
        }

        views.rcvGallery.setOnTouchListener { view, motionEvent ->
            context?.hideKeyboard(views.root)
            false
        }

        views.imgCamera.setOnClickListener {
            handleBottomGallery(false)
            context?.hideKeyboard(views.root)
            checkPermisionCamera {
                if (it){
                    sendCameraPhoto()
                }else{
                    showSnackbar(views.root, getString(R.string.access_camera), false, getString(R.string.to_setting)){
                        activity?.startToDetailPermission()
                    }
                }
            }
        }

        views.layoutHeaderGallery.setOnClickListener {
            if (bottomBehavior.state == STATE_COLLAPSED) {
                bottomBehavior.state = STATE_EXPANDED
            } else {
                bottomBehavior.state = STATE_COLLAPSED
            }
            context?.hideKeyboard(views.root)
        }

        views.btnPerGaller.setOnClickListener {
            checkPermissionGallery {
                if (it) {
                    checkResutlPerGallery(it)
                } else {
                    showSnackbar(views.root, getString(R.string.access_gallery), false, getString(R.string.to_setting)){
                        activity?.startToDetailPermission()
                    }
                }
            }
        }

        views.imgCall.setOnClickListener{
            val idTargetUser: String? = withState(chatViewmodel){ it.curentRoom.invoke()?.shopUserId?._id}
            val intent = Intent(requireContext(), CallActivity::class.java)
            requireActivity().startActivityWithData(intent, MyConfigNotifi.TYPE_CALL_OFFER, idTargetUser)
        }

        views.imgCallVideo.setOnClickListener{
            startActivity(Intent(requireContext(), CallActivity::class.java))
        }
    }

    private fun handleBottomGallery(isVisible: Boolean) {
        // nếu kh hiện
        if (isVisible) {
            views.layoutBody.setMargins(0, 0, 0, (displaySize.heightPixels * 0.3).toInt())
            views.layoutBehavior.isVisible = true
        } else {
            views.layoutBody.setMargins(0, 0, 0, 0)
            views.layoutBehavior.isVisible = false
        }

        // xóa list đã chọn
        listSelectGallery.clear()
        views.btnSendImage.isVisible = listSelectGallery.size > 0
        views.rcvGallery.scrollToPosition(0)
        adapterGallery?.notifyDataSetChanged()

        views.imgGallery.setImageResource(if (!views.layoutBehavior.isVisible) R.drawable.icon_gallery else R.drawable.icon_gallery_select)
    }

    private fun handleSendMessage() {
        var roomId = withState(chatViewmodel) { it.curentRoom.invoke() }
        if (views.edtMessage.text.toString().isNotEmpty() && roomId != null) {
            var message = Message(
                null,
                roomId._id,
                null,
                message = views.edtMessage.text.toString(),
                null,
                null,
                MessageType.TYPE_TEXT,
                arrayListOf(),
                arrayListOf(),
                null, null
            )
            chatViewmodel.handle(ChatViewAction.postMessage(message, null))
            views.edtMessage.setText("")
        }
    }

    private fun handleSendMessageImages() {
        val roomId = withState(chatViewmodel) { it.curentRoom.invoke() }
        if (roomId != null) {
            val message = Message(
                null,
                roomId._id,
                null,
                message = "Ảnh",
                null,
                null,
                MessageType.TYPE_IMAGE,
                arrayListOf(),
                arrayListOf(),
                null,
                null
            )
            chatViewmodel.handle(ChatViewAction.postMessage(message, listSelectGallery, pathPhoto))

            this.pathPhoto = ""
            this.listSelectGallery.clear()
            views.btnSendImage.isVisible = listSelectGallery.size > 0
        }
    }


    private fun setupViewModel() {
    }

    private fun showScreenPhoto(messages: Array<Image>?) {
        StfalconImageViewer.Builder(requireContext(), messages) { view, image ->
            Glide.with(view).load(image.url).into(view)
        }
            .withBackgroundColorResource(R.color.black)
            .withHiddenStatusBar(true)
            .show()
    }

    private fun showScreenPhoto(galleyImages: Array<GalleyImage>?) {
        StfalconImageViewer.Builder(requireContext(), galleyImages) { view, image ->
            var uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image.id)
            Glide.with(view).load(uri).into(view)
        }
            .withBackgroundColorResource(R.color.black)
            .withHiddenStatusBar(true)
            .show()
    }

    private fun showDialogVideo(video: GalleryVideo) {
        var dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor)
        var bindingDialog = DialogVideoBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        dialog.show()

        // media player
        val exoPlayer = ExoPlayer.Builder(requireContext()).build()
        exoPlayer.apply {
            setMediaItem(MediaItem.fromUri(video.realPath))
            prepare()
            playWhenReady = true
        }
        bindingDialog.viewPlayer.player = exoPlayer

        dialog.setOnDismissListener {
            exoPlayer.pause()
            exoPlayer.release()
        }

        bindingDialog.imgBack.setOnClickListener{
            dialog.dismiss()
        }

//        bindingDialog.imgOption.setOnClickListener{
//            showDialogPopup(bindingDialog.imgOption, video)
//        }

        bindingDialog.viewPlayer.setControllerVisibilityListener {
            (it == 0).let{
                bindingDialog.imgBack.isVisible = it
//                bindingDialog.imgOption.isVisible = it
            }
        }

        bindingDialog.viewPlayer.videoSurfaceView?.setOnClickListener{
            if (bindingDialog.viewPlayer.isControllerVisible) bindingDialog.viewPlayer.hideController()
            else bindingDialog.viewPlayer.showController()
        }

    }

    override fun onDestroy() {
        chatViewmodel.handle(ChatViewAction.returnOffEventMessageSocket(withState(chatViewmodel) {
            it.curentRoom.invoke()?._id ?: ""
        }))
        chatViewmodel.handle(ChatViewAction.removeCurrentChat)
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun invalidate(): Unit = withState(chatViewmodel) {
        when (it.curentRoom) {
            is Success -> {
                adapter.setDataRoom(it.curentRoom.invoke())
                views.layoutHeader.tvTitleToolbar.text = "${it.curentRoom.invoke().shopUserId?.last_name} ${it.curentRoom.invoke().shopUserId?.first_name}"
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(R.string.room_not_found), Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }
            else -> {
            }
        }

        when (it.curentMessage) {
            is Success -> {
                adapter.setDataMessage(it.curentMessage.invoke())
                views.rcvChat.scrollToPosition(it.curentMessage.invoke().size - 1)
            }

            else -> {
            }
        }

        when (it.messageSent) {
            is Success -> {
                if (adapter != null) {
                    views.imgSending.isVisible = false
                    views.rcvChat.setMargins(0, 0, 0, 0)
                }
            }

            is Fail -> {
                views.imgSending.isVisible = false
                views.rcvChat.setMargins(0, 0, 0, 0)
                showSnackbar(views.root, getString(R.string.send_message_faild), false, null) {}
            }

            is Loading -> {
                views.rcvChat.setMargins(0, 0, 0, 30)
                views.imgSending.isVisible = true
            }

            else -> {
            }
        }

        when (it.newMessage) {
            is Success -> {
                if (adapter != null) {
                    var sizeList = adapter!!.addData(it.newMessage.invoke())
                    views.rcvChat.scrollToPosition(sizeList ?: 0)
                    chatViewmodel.handle(ChatViewAction.removePostMessage)
                }
            }

            else -> {
            }
        }

        when (it.galleries) {
            is Success -> {
                adapterGallery?.setData(it.galleries.invoke())
            }
            else -> {
            }
        }
    }

    private fun listenOnBackPress() {
        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (views.layoutBehavior.isVisible) {
                    if (bottomBehavior.state == STATE_EXPANDED) {
                        bottomBehavior.state = STATE_COLLAPSED
                    } else {
                        handleBottomGallery(false)
                    }
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)
    }

    private fun checkResutlPerGallery(isAllow: Boolean) {
        if (isAllow) {
            chatViewmodel.handle(ChatViewAction.getDataGallery)
            views.rcvGallery.isVisible = true
            views.layoutNoPerGallery.isVisible = false
        } else {
            views.rcvGallery.isVisible = false
            views.layoutNoPerGallery.isVisible = true
        }
    }

    private fun sendCameraPhoto(): Uri?{
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "camera_photo.jpg")
        }

        var currentPhotoUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        currentPhotoUri?.let {
            val intentPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, it)
            takePictureLauncher.launch(it)
        }

        pathPhoto = requireContext().uriToFilePath(currentPhotoUri)

        return currentPhotoUri
    }

}

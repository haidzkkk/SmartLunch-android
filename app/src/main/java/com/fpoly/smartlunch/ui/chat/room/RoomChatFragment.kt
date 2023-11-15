package com.fpoly.smartlunch.ui.chat.room

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
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
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Image
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.MessageType
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.FragmentRoomChatBinding
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ultis.checkPermissionGallery
import com.fpoly.smartlunch.ultis.hideKeyboard
import com.fpoly.smartlunch.ultis.showSnackbar
import com.fpoly.smartlunch.ultis.startToDetailPermission
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.stfalcon.imageviewer.StfalconImageViewer
import java.io.File


class RoomChatFragment : PolyBaseFragment<FragmentRoomChatBinding>() {

    val displaySize = DisplayMetrics()
    lateinit var bottomBehavior: BottomSheetBehavior<LinearLayout>

    var adapter: RoomChatAdapter? = null
    var adapterGallery: GalleryBottomChatAdapter? = null
    val chatViewmodel: ChatViewmodel by activityViewModel()

    var listSelectGallery: ArrayList<Gallery> = arrayListOf()

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
        chatViewmodel.initObserverPeerConnection()
        views.layoutHeader.imgBack.isVisible = true
    }

    private fun setupRcv() {
        var user: User? = withState(chatViewmodel) { it.curentUser.invoke() }
        if (user != null) {
            adapter = RoomChatAdapter(user, object : RoomChatAdapter.IOnClickLisstenner {
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

                }

            })
        }

        if (adapter != null) {
            views.rcvChat.adapter = adapter
            views.rcvChat.recycledViewPool.clear()
            views.rcvChat.layoutManager = LinearLayoutManager(requireContext())
        } else {
            showSnackbar(views.root, "Không có thông tin của bạn", false, "Quay lại") {}
        }
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
                    showSnackbar(views.root, "Bạn chưa cho quyền truy cập ảnh", false, "Đến cài đặt"){
                        activity?.startToDetailPermission()
                    }
                }
            }
        }

        views.imgCall.setOnClickListener{
            chatViewmodel.setCallVideoWithUser(withState(chatViewmodel){ it.curentRoom.invoke()!!.shopUserId})
            findNavController().navigate(R.id.callChatFragment)
            chatViewmodel.sendCallToServer(RequireCall(RequireCallType.START_CALL, null, null, null))
        }
    }

    private fun handleBottomGallery(isVisible: Boolean) {
        // nếu kh hiện
        if (isVisible) {
            setMargins(views.layoutBody, 0, 0, 0, (displaySize.heightPixels * 0.3).toInt())
            views.layoutBehavior.isVisible = true
        } else {
            setMargins(views.layoutBody, 0, 0, 0, 0)
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
                null
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
                null
            )
            chatViewmodel.handle(ChatViewAction.postMessage(message, listSelectGallery))

            listSelectGallery.clear()
            views.btnSendImage.isVisible = listSelectGallery.size > 0
        }
    }

    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val marginLayoutParams = view.layoutParams as MarginLayoutParams
            marginLayoutParams.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    private fun setupViewModel() {
    }

    private fun showScreenPhoto(messages: Array<Image>?) {
        StfalconImageViewer.Builder<Image>(requireContext(), messages) { view, image ->
            Glide.with(view).load(image.url).into(view)
        }
            .withBackgroundColorResource(R.color.black)
            .withHiddenStatusBar(true)
            .show()
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
                views.layoutHeader.tvTitleToolbar.text = "${it.curentRoom.invoke().shopUserId?.last_name} ${it.curentRoom.invoke().shopUserId?.first_name}"
            }
            is Fail -> {
                showSnackbar(views.root, "Không có your user", false, "Quay lại") {
                }
            }
            else -> {
            }
        }

        when (it.curentMessage) {
            is Success -> {
                if (adapter != null) {
                    adapter!!.setData(it.curentMessage.invoke())
                    views.rcvChat.scrollToPosition(it.curentMessage.invoke().size - 1)
                }
            }

            else -> {
            }
        }

        when (it.messageSent) {
            is Success -> {
                if (adapter != null) {
                    views.imgSending.isVisible = false
                    setMargins(views.rcvChat, 0, 0, 0, 0)
                }
            }

            is Fail -> {
                views.imgSending.isVisible = false
                setMargins(views.rcvChat, 0, 0, 0, 0)
                showSnackbar(views.root, "Giu không thành công", false, null) {}
            }

            is Loading -> {
                setMargins(views.rcvChat, 0, 0, 0, 30)
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

}

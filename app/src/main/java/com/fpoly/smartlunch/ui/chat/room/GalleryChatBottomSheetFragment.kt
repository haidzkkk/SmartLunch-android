package com.fpoly.smartlunch.ui.chat.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.databinding.BottomSheetGalleryChatBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewAction
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ultis.checkPermissionGallery
import com.fpoly.smartlunch.ultis.showSnackbar
import com.fpoly.smartlunch.ultis.startToDetailPermission

class GalleryBottomSheetFragment(private val itemSelect: (list: ArrayList<Gallery>) -> Unit) : PolyBaseBottomSheet<BottomSheetGalleryChatBinding>() {
    val homeViewModel: HomeViewModel by activityViewModel()

    lateinit var gallertAdapter: GalleryBottomChatAdapter

    var listSelect = arrayListOf<Gallery>()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetGalleryChatBinding = BottomSheetGalleryChatBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        listenEvent()
    }
    private fun initUI() {
        gallertAdapter = GalleryBottomChatAdapter(object : GalleryBottomChatAdapter.IOnClickLisstenner{
            override fun onClickSelectItem(gallery: Gallery) {
                listSelect.add(gallery)
            }

            override fun onClickUnSelectItem(gallery: Gallery) {
                listSelect.remove(gallery)
            }

            override fun onLongClickItem(gallery: Gallery) {
            }
        })

        views.rcv.adapter = gallertAdapter
        views.rcv.layoutManager = GridLayoutManager(requireContext(), 4)

        checkPermissionGallery {
            if (it){
                homeViewModel.handle(HomeViewAction.getDataGallery)
            }else{
                showSnackbar(views.root, "Bạn chưa cho quyền truy cập ảnh", false, "Đến cài đặt"){
                    activity?.startToDetailPermission()
                }
            }
        }
    }

    private fun listenEvent() {
        views.tvDone.setOnClickListener{
            itemSelect(listSelect)
            this.dismiss()
        }
    }


    override fun invalidate() {
        withState(homeViewModel){
            when(it.galleries){
                is Success ->{
                    gallertAdapter.setData(it.galleries.invoke())
                }
                else ->{
                    Toast.makeText(requireContext(), "Không thể lấy ảnh từ thư viện", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
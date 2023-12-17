package com.fpoly.smartlunch.ui.main.comment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Comment
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.FragmentCommentBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterSize
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel

class CommentFragment : PolyBaseFragment<FragmentCommentBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()
    private lateinit var commentAdapter: CommentAdapter

    private var currentProduct: Product? = null
    private var isSort: Boolean = false

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCommentBinding = FragmentCommentBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        listenEvent()
    }

    private fun initUI() {
        views.layoutHeader.btnBackToolbar.isVisible = true
        views.layoutHeader.tvTitleToolbar.text = getString(R.string.evaluate)

        commentAdapter = CommentAdapter()
        views.rcvComment.adapter = commentAdapter
        views.rcvComment.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        productViewModel.handle(ProductAction.GetListComments(withState(productViewModel){it.asyncProduct.invoke()?._id}))
        handleBackChip(0)
    }

    private fun listenEvent() {
        views.layoutHeader.btnBackToolbar.setOnClickListener{
            activity?.onBackPressed()
        }
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetListComments(currentProduct?._id))
            productViewModel.handle(ProductAction.GetListComments(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""}))
        }
        views.tvSelectAll.setOnClickListener{
            productViewModel.handle(ProductAction.GetListComments(currentProduct?._id))
            handleBackChip(0)
        }
        views.tvSelectImage.setOnClickListener{
            productViewModel.handle(ProductAction.GetListComments(currentProduct?._id, isImage = true))
            handleBackChip(1)
        }
        views.layoutSelectRate.setOnClickListener{
            handleBackChip(2)
            CommentRateBottom().show(requireActivity().supportFragmentManager, "CommentRateBottom")
        }
        views.layoutSort.setOnClickListener{
            isSort = !isSort
            views.tvSort.text = if (isSort) getString(R.string.increase) else getString(R.string.decrease)
            commentAdapter.sortData(isSort)
            handleBackChip(3)
        }
    }
    override fun invalidate(){

        withState(productViewModel){
            views.swipeLoading.isRefreshing = it.asyncComments is Loading

            when (it.asyncProduct) {
                is Success -> {
                    it.asyncProduct.invoke()?.let { product ->
                        currentProduct = product
                    }
                }

                else -> {}
            }
            when(it.asyncComments){
                is Success ->{
                    commentAdapter.setData(it.asyncComments.invoke())
                }
                else ->{

                }
            }
        }
    }

    fun handleBackChip(index: Int){
        views.tvSelectAll.setBackgroundResource(R.drawable.backgound_retangle_chip)
        views.tvSelectImage.setBackgroundResource(R.drawable.backgound_retangle_chip)
        views.layoutSelectRate.setBackgroundResource(R.drawable.backgound_retangle_chip)
        views.layoutSort.setBackgroundResource(R.drawable.backgound_retangle_chip)

        views.tvSelectAll.setTextColor(requireActivity().getColor(R.color.black))
        views.tvSelectImage.setTextColor(requireActivity().getColor(R.color.black))
        views.tvSelectRate.setTextColor(requireActivity().getColor(R.color.black))
        views.tvSort.setTextColor(requireActivity().getColor(R.color.black))

        when(index){
            0 -> {
                views.tvSelectAll.setTextColor(requireActivity().getColor(R.color.red))
                views.tvSelectAll.setBackgroundResource(R.drawable.backgound_retangle_chip_select)
            }
            1 -> {
                views.tvSelectImage.setTextColor(requireActivity().getColor(R.color.red))
                views.tvSelectImage.setBackgroundResource(R.drawable.backgound_retangle_chip_select)
            }
            2 -> {
                views.tvSelectRate.setTextColor(requireActivity().getColor(R.color.red))
                views.layoutSelectRate.setBackgroundResource(R.drawable.backgound_retangle_chip_select)
            }
            3 -> {
                views.tvSort.setTextColor(requireActivity().getColor(R.color.red))
                views.layoutSort.setBackgroundResource(R.drawable.backgound_retangle_chip_select)
            }

        }
    }

}
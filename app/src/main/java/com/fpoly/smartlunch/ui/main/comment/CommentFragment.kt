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
import com.fpoly.smartlunch.databinding.FragmentCommentBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterSize
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel

class CommentFragment : PolyBaseFragment<FragmentCommentBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()
    private lateinit var commentAdapter: CommentAdapter
    private var mListComment: ArrayList<Comment>? = null

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

        productViewModel.handle(ProductAction.GetListComments(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""}))
        handleBackChip(0)
    }

    private fun listenEvent() {
        views.layoutHeader.btnBackToolbar.setOnClickListener{
            activity?.onBackPressed()
        }
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetListComments(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""}))
        }
        views.tvSelectAll.setOnClickListener{
            commentAdapter.setData(mListComment)
            handleBackChip(0)
        }
        views.tvSelectImage.setOnClickListener{
            var list = mListComment?.filter { it.images?.isNotEmpty() == true }?.toList()
            commentAdapter.setData(list)
            handleBackChip(1)
        }
        views.layoutSelectRate.setOnClickListener{
            handleBackChip(2)
        }
        views.layoutSelectSize.setOnClickListener{
            handleBackChip(3)
        }
    }
    override fun invalidate(){

        withState(productViewModel){
            views.swipeLoading.isRefreshing = it.asyncComments is Loading

            when(it.asyncComments){
                is Success ->{
                    mListComment = it.asyncComments.invoke()
                    commentAdapter.setData(mListComment)
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
        views.layoutSelectSize.setBackgroundResource(R.drawable.backgound_retangle_chip)

        views.tvSelectAll.setTextColor(requireActivity().getColor(R.color.black))
        views.tvSelectImage.setTextColor(requireActivity().getColor(R.color.black))
        views.tvSelectRate.setTextColor(requireActivity().getColor(R.color.black))
        views.tvSelectSize.setTextColor(requireActivity().getColor(R.color.black))

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
                views.tvSelectSize.setTextColor(requireActivity().getColor(R.color.red))
                views.layoutSelectSize.setBackgroundResource(R.drawable.backgound_retangle_chip_select)
            }

        }
    }

}
package com.fpoly.smartlunch.ui.main.comment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Comment
import com.fpoly.smartlunch.databinding.ItemCommentBinding
import com.fpoly.smartlunch.ultis.StringUltis.dateDay2TimeFormat
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.convertToStringFormat

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){
    var mListComment: List<Comment>? = null

    fun setData(data: List<Comment>?){
        this.mListComment = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(comment: Comment){
            Glide.with(binding.root.context).load(comment.userId?.avatar?.url ?: "").placeholder(R.drawable.icon_user).into(binding.imgAvatar)
            binding.tvName.text = comment.userId?._id
            handleRateVote(binding, comment)

            binding.tvMessageSize.text = comment.sizeName
            binding.tvMessage.text = comment.description
            binding.tvDate.text = comment.createdAt.convertIsoToStringFormat(dateDay2TimeFormat)

            if (!comment.images.isNullOrEmpty()){
                binding.rcvImg.isVisible = true
                var adapter = ImageAdapter()
                adapter.setData(comment.images)
                binding.rcvImg.adapter = adapter
                binding.rcvImg.layoutManager = GridLayoutManager(binding.root.context, 4)
            }else{
                binding.rcvImg.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = if (mListComment != null) mListComment!!.size else 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListComment != null){
            holder.onBind(mListComment!![position])
        }
    }

    fun handleRateVote(binding: ItemCommentBinding, comment: Comment){
        var context = binding.root.context
        binding.start1.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start2.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start3.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start4.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start5.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)

        when (comment.rating){
            1 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            2 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            3 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start3.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            4 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start3.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start4.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            5 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start3.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start4.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start5.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
    }
}
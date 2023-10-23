package com.fpoly.smartlunch.ui.chat.room

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.MessageType
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.ItemChatErrorBinding
import com.fpoly.smartlunch.databinding.ItemChatMeBinding
import com.fpoly.smartlunch.databinding.ItemChatYouBinding
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertToStringFormat

class RoomChatAdapter(
    private val myUser: User,
) : RecyclerView.Adapter<RoomChatAdapter.ViewHolder>() {

    companion object{
        const val TYPE_ME = 0
        const val TYPE_YOU = 1
    }

    var messages: ArrayList<Message> = ArrayList()

    fun setData(data: ArrayList<Message>?){
        if (data == null) return
        messages = data
        notifyDataSetChanged()
    }

    fun addData(data: Message?){
        if (data == null) return
        messages.add(data)
        notifyItemInserted(messages.size - 1)
        Log.e("TAG", "${messages.size - 1} addData: $data", )
    }

    inner class ViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(message: Message, position: Int){
            when(binding){
                is ItemChatMeBinding ->{
                    handChatMe(binding, message, position)
                }
                is ItemChatYouBinding ->{
                    handChatYou(binding, message, position)
                }
                is ItemChatErrorBinding ->{

                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (messages[position].userIdSend?._id == myUser._id) TYPE_ME else TYPE_YOU
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_ME)return ViewHolder(ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return ViewHolder(ItemChatYouBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(messages[position], position)
    }


    private fun handChatMe(binding: ItemChatMeBinding, message: Message, position: Int) {
        if ( position + 1 < messages.size && messages[position].userIdSend?._id == messages[position + 1].userIdSend?._id){
            binding.imgAvatar.isVisible = false
        }else{
            binding.imgAvatar.isVisible = true
            binding.imgAvatar.setImageResource(R.mipmap.ic_launcher)
        }

        binding.tvTime.text = message.time?.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateTimeHourFormat)
        binding.tvMessage.text = message.message

//        if (message.type == MessageType.TYPE_IMAGE && message.images?.get(0) != null){
//            binding.imgMassage.apply {
//                isVisible = true
//                Glide.with(this.context).load(message.images[0].url).into(this)
//            }
//        }
    }


    private fun handChatYou(binding: ItemChatYouBinding, message: Message, position: Int) {
        if ( position + 1 < messages.size && messages[position].userIdSend?._id == messages[position + 1].userIdSend?._id){
            binding.imgAvatar.isVisible = false
        }else{
            binding.imgAvatar.isVisible = true
            binding.imgAvatar.setImageResource(R.mipmap.ic_launcher)
        }

        binding.tvTime.text = message.time?.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateTimeHourFormat)
        binding.tvMessage.text = message.message

//        if (message.type == MessageType.TYPE_IMAGE && message.images?.get(0) != null){
//            binding.imgMassage.apply {
//                isVisible = true
//                Glide.with(this.context).load(message.images[0].url).into(this)
//            }
//        }
    }
}
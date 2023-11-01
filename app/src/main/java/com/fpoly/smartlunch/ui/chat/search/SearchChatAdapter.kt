package com.fpoly.smartlunch.ui.chat.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.ItemSearchChatBinding

class SearchChatAdapter(private val onClickItem : (user: User) -> Unit) : RecyclerView.Adapter<SearchChatAdapter.ViewHolder>() {

    private var users: ArrayList<User> = arrayListOf()

    fun setData(data: ArrayList<User>?){
        if (data.isNullOrEmpty()) return
        this.users = data
        notifyDataSetChanged()
    }

    fun clearData(){
        this.users.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemSearchChatBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(user: User){
            Glide.with(binding.root).load("user.images").placeholder(R.mipmap.ic_launcher).into(binding.imgAvatar)
            binding.tvName.text = "${user.last_name} ${user.first_name}"

            binding.root.setOnClickListener{
                onClickItem(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemSearchChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(users[position])
    }
}
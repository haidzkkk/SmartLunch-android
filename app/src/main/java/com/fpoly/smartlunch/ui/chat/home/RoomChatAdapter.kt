package com.fpoly.smartlunch.ui.chat.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.ItemRoomBinding
import com.fpoly.smartlunch.ultis.StringUltis.dateIso8601Format
import com.fpoly.smartlunch.ultis.StringUltis.dateTimeDayFormat
import com.fpoly.smartlunch.ultis.convertToStringFormat

@SuppressLint("SetTextI18n")
class RoomChatAdapter(
//    private val curentUser: User
    private val callBack: IOnClickRoomChatListenner

) : RecyclerView.Adapter<RoomChatAdapter.ViewHolder>() {
    interface IOnClickRoomChatListenner{
        fun onClickItem(room: Room)
        fun onLongClickItem(room: Room)
    }

    var rooms: ArrayList<Room> = ArrayList()

    fun setData(data: ArrayList<Room>?){
        if (data == null) return
        this.rooms = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(room: Room) {
            with(binding as ItemRoomBinding){
                this.imgAvatar.setImageResource(R.drawable.logo_app)
                this.tvDisplayName.text = "${room.shopUserId?.first_name} ${room.shopUserId?.last_name}"
                this.tvMessage.text = room.messSent
                this.tvTime.text = room.timeSent?.convertToStringFormat(dateIso8601Format, dateTimeDayFormat)

                binding.root.setOnClickListener{ callBack.onClickItem(room)}
                binding.root.setOnLongClickListener(){
                    callBack.onLongClickItem(room)
                    true
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = rooms.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rooms[position])
    }
}
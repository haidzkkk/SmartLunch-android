package com.fpoly.smartlunch.ui.main.notification.adapter
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Notification
import com.fpoly.smartlunch.databinding.NotificationItemBinding

class NotificationAdapter(private val onClickItem: (Notification) -> Unit) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private var notifications: List<Notification> = listOf()

    fun setData(list: List<Notification>?){
        if (list!=null){
            notifications=list
            notifyDataSetChanged()
        }
    }
    inner class NotificationViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.apply {
                if (notification.isRead){
                    root.setBackgroundColor(Color.WHITE)
                }
                title.text = notification.title
                content.text = notification.content
                time.text = notification.timestamp
            }
            binding.root.setOnClickListener {
              onClickItem(notification)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}

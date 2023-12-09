package com.fpoly.smartlunch.ui.main.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.databinding.ItemOrderBinding
import com.fpoly.smartlunch.ultis.Status
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.setTextColor

class OrderAdapter(private val onClickItem: (String) -> Unit): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var orders: List<OrderResponse> = listOf()

    fun setData(list: List<OrderResponse>?){
        if (list != null){
            orders=list
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = ItemOrderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]
        holder.bind(currentOrder)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    inner class OrderViewHolder(private val itemOrderBinding: ItemOrderBinding) : RecyclerView.ViewHolder(itemOrderBinding.root) {
        fun bind(order: OrderResponse) {
            itemOrderBinding.root.setOnClickListener {
                onClickItem(order._id)
            }
            itemOrderBinding.idOrder.text = order._id
            itemOrderBinding.status.text = order.status.status_name
            itemOrderBinding.nameProduct.text = if(order.products.isNotEmpty()) order.products[0].product_name else ""
            itemOrderBinding.price.text = order.totalAll.formatCash()
            itemOrderBinding.quanlity.text = order.products.size.toString()
            itemOrderBinding.status.setTextColor(order.status._id == Status.SUCCESS_STATUS)
            itemOrderBinding.tvDate.text = order.createdAt.convertIsoToStringFormat(StringUltis.dateTimeDateFormat)
        }
    }
}

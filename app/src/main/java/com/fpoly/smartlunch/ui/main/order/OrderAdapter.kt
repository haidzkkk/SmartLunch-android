package com.fpoly.smartlunch.ui.main.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.databinding.ItemOrderBinding

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
            itemOrderBinding.nameProduct.text = order.products[0].product_name
            itemOrderBinding.price.text = "${order.total} đ"
            itemOrderBinding.quanlity.text = order.products.size.toString()
        }
    }
}

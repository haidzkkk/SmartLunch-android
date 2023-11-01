package com.fpoly.smartlunch.ui.main.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.databinding.ItemOrderBinding

class OrderAdapter(): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
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
            itemOrderBinding.idOrder.text = order._id
            itemOrderBinding.status.text = convertIdToStatus(order.status)
            itemOrderBinding.nameProduct.text = order.products[0].product_name
            itemOrderBinding.price.text = "${order.total} đ"
            itemOrderBinding.quanlity.text = order.products.size.toString()
        }
        private fun convertIdToStatus(statusId: String): String {
            val statusMap = mapOf(
                "65264bc32d9b3bb388078974" to "Đang chờ xác nhận",
                "65264c102d9b3bb388078976" to "Đã xác nhận đơn hàng",
                "65264c672d9b3bb388078978" to "Đang giao hàng",
                "6526a6e6adce6a54f6f67d7d" to "Giao hàng thành công",
                "653bc0a72006e5791beab35b" to "Hủy đơn hàng thành công"
            )

            return statusMap[statusId] ?: "Không xác định"
        }
    }
}

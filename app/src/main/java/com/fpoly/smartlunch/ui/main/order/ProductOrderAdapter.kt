package com.fpoly.smartlunch.ui.main.order


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.ProductOrder
import com.fpoly.smartlunch.databinding.ItemProductOrderBinding
import com.fpoly.smartlunch.ultis.formatCash


class ProductOrderAdapter(private val onPress: (productOrder: ProductOrder) -> Unit): RecyclerView.Adapter<ProductOrderAdapter.ViewHolder>() {
    var orderResporn: OrderResponse? = null

    fun setData(data: OrderResponse?){
        if (data == null) return
        this.orderResporn = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemProductOrderBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(productOrder: ProductOrder){
            binding.apply{
                Glide.with(this.root.context).load(productOrder.image).placeholder(R.mipmap.ic_launcher).into(imgImg)
                tvName.text = productOrder.product_name
                tvType.text = "Size ${productOrder.sizeName}"
                tvQuantity.text = "x${productOrder.purchase_quantity}"
                tvPrice.text = productOrder.product_price.formatCash()

//                if (productOrder.product_discount < productOrder.product_price){
//                    tvPrice.paintFlags = tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                    tvDiscount.text = productOrder.product_discount.formatCash()
//                }else{
//                    tvPrice.paintFlags = tvPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
//                    tvDiscount.text = ""
//                }

                tvAllPrice.text = (productOrder.product_price * productOrder.purchase_quantity).formatCash()
                
                root.setOnClickListener {
                    onPress(productOrder)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        if (orderResporn?.products != null) return orderResporn?.products!!.size
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (orderResporn?.products != null){
            holder.onBind(orderResporn?.products!!.get(position))
        }
    }
}
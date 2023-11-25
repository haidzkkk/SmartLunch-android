package com.fpoly.smartlunch.ui.main.order

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ProductOrder
import com.fpoly.smartlunch.databinding.ItemProductReviewBinding
import com.fpoly.smartlunch.ultis.StringUltis.dateDay2TimeFormat
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.formatCash

class ProductReviewAdapter(private val onClickProduct: (productOrder: ProductOrder) -> Unit,
                           private val onPress: (productOrder: ProductOrder) -> Unit):
    RecyclerView.Adapter<ProductReviewAdapter.ViewHolder>() {
    var orderResporn: OrderResponse? = null

    fun setData(data: OrderResponse?){
        if (data == null) return
        this.orderResporn = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemProductReviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(productOrder: ProductOrder){
            binding.apply{
                Glide.with(this.root.context).load(productOrder.image).placeholder(R.mipmap.ic_launcher).into(imgImg)
                tvName.text = productOrder.product_name
                tvType.text = "Size ${productOrder.sizeName}"
                tvQuantity.text = "x${productOrder.purchase_quantity}"
                tvPrice.text = productOrder.product_price.formatCash()
                tvAllPrice.text = (productOrder.product_discount * productOrder.purchase_quantity).formatCash()

                if (productOrder.product_discount < productOrder.product_price){
                    tvPrice.paintFlags = tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvDiscount.text = productOrder.product_discount.formatCash()
                }else{
                    tvPrice.paintFlags = tvPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvDiscount.text = ""
                }

                if (orderResporn != null){
                    tvDate.text = orderResporn!!.createdAt.convertIsoToStringFormat(dateDay2TimeFormat)
                    tvStatus.text = orderResporn!!.status.status_name
                }

                root.setOnClickListener {
                    onClickProduct(productOrder)
                }

                btnReview.setOnClickListener{
                    onPress(productOrder)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
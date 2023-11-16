package com.fpoly.smartlunch.ui.main.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.ItemProductReviewBinding
import com.fpoly.smartlunch.ultis.StringUltis.dateDay2TimeFormat
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.formatCash

class ProductReviewAdapter(private val onPress: (productCart: ProductCart) -> Unit): RecyclerView.Adapter<ProductReviewAdapter.ViewHolder>() {
    var orderResporn: OrderResponse? = null

    fun setData(data: OrderResponse?){
        if (data == null) return
        this.orderResporn = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemProductReviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(productCart: ProductCart){
            binding.apply{
                Glide.with(this.root.context).load(productCart.image).placeholder(R.mipmap.ic_launcher).into(imgImg)
                tvName.text = productCart.product_name
                tvType.text = "Size ${productCart.sizeName}"
                tvQuantity.text = "x${productCart.purchase_quantity}"
                tvPrice.text = productCart.product_price.toDouble().formatCash()
                tvAllPrice.text = (productCart.product_price * productCart.purchase_quantity).toDouble().formatCash()

                if (orderResporn != null){
                    tvDate.text = orderResporn!!.createdAt.convertIsoToStringFormat(dateDay2TimeFormat)
                    tvStatus.text = orderResporn!!.status.status_name
                }

                btnReview.setOnClickListener{
                    onPress(productCart)
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
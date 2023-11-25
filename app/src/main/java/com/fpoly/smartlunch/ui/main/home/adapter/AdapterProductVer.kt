package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemNewLayoutVerBinding
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.formatRate
import com.fpoly.smartlunch.ultis.formatView

@SuppressLint("SetTextI18n")
class AdapterProductVer(private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<AdapterProductVer.ProductViewHolder>() {

    private var products: List<Product> = listOf()

    fun setData(list: List<Product>?){
        if (list != null){
            products = list
            notifyDataSetChanged()
        }
    }

    inner class ProductViewHolder(private val binding: ItemNewLayoutVerBinding,val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentProduct: Product){
            binding.apply {
                Glide.with(context)
                    .load( if(currentProduct.images.isNotEmpty()) currentProduct.images[0].url else "")
                    .placeholder(R.drawable.loading_img)
                    .error(R.drawable.loading_img)
                    .into(image)

                nameProduct.text = currentProduct.product_name
                priceProduct.text = currentProduct.product_price.formatCash()
                tvRate.text = " ${currentProduct.rate.formatRate()} đánh giá"
                tvBuy.text = "đã bán ${currentProduct.bought.formatView()}"

                layoutItemProduct.setOnClickListener {
                    onClickItem(currentProduct._id)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewLayoutVerBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding,parent.context)

    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val currentProduct: Product = products[position]
            holder.bind(currentProduct)

    }

    override fun getItemCount(): Int {
        if (products.size < 5)
            return products.size
        return 5
    }
}
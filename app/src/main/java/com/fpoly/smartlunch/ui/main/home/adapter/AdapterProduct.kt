package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemProductLayoutHozBinding
import com.fpoly.smartlunch.databinding.ItemSeeMoreBinding
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.formatRate
import com.fpoly.smartlunch.ultis.formatView
import com.fpoly.smartlunch.ultis.setMargins


@SuppressLint("SetTextI18n")
class AdapterProduct(private val onClickItem: OnClickListenner) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnClickListenner{
        fun onCLickItem(id: String)
        fun onCLickSeeMore()
    }

    private var products: List<Product> = listOf()

    fun setData(list: List<Product>?){
        if (list != null){
            products = list
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        if (products.size < 5) return products.size + 1
        return 6
    }

    override fun getItemViewType(position: Int): Int {
        if (position == (itemCount - 1)) return 1
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = if (viewType == 1) SeeMoreViewHolder(ItemSeeMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else ProductViewHolder(ItemProductLayoutHozBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is ProductViewHolder ->{
                holder.bind(products[position], position)
            }
            is SeeMoreViewHolder ->{
                holder.bind()
            }
        }

    }
    inner class ProductViewHolder(private val binding: ItemProductLayoutHozBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentProduct : Product, position: Int ) {
            binding.apply {
                Glide.with(root.context)
                    .load( if(currentProduct.images.isNotEmpty()) currentProduct.images[0].url else "")
                    .placeholder(R.drawable.loading_img)
                    .error(R.drawable.loading_img)
                    .into(image)
                nameProduct.text = currentProduct.product_name
                priceProduct.text = currentProduct.product_price.toDouble().formatCash()
                root.setOnClickListener {
                    onClickItem.onCLickItem(currentProduct._id)
                }
                tvRate.text = " ${currentProduct.rate.formatRate()} (${currentProduct.rate_count})"
                tvBuy.text = "đã bán ${currentProduct.bought.formatView()}"

                if (position == products.size -1 ) binding.layoutMain.setMargins(0, 0, 30, 0)
                else binding.layoutMain.setMargins(0, 0, 0, 0)

                binding.layoutSale.isVisible = false
            }
        }
    }
    inner class SeeMoreViewHolder(private val binding: ItemSeeMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener{
                onClickItem.onCLickSeeMore()
            }
        }
    }
}
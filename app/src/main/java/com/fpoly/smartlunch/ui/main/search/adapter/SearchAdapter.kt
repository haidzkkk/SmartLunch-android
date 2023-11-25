package com.fpoly.smartlunch.ui.main.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemSearchBinding
import com.fpoly.smartlunch.ultis.formatCash

class SearchAdapter (private val onClickItem : (product: Product) -> Unit) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var products: ArrayList<Product> = arrayListOf()

    fun setData(data: ArrayList<Product>?){
        if (data.isNullOrEmpty()) return
        this.products = data
        notifyDataSetChanged()
    }

    fun clearData(){
        this.products.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemSearchBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(product: Product){
            binding.tvName.text = "${product.product_name}"
            Glide.with(binding.root.context)
                .load( if(product.images.isNotEmpty()) product.images[0].url else "")
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.loading_img)
                .into(binding.image)
            binding.priceProduct.text = product.product_price.toDouble().formatCash()
            binding.root.setOnClickListener{
                onClickItem(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(products[position])
    }
}
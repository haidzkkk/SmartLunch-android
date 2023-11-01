package com.fpoly.smartlunch.ui.main.home.adapter

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

class AdapterProductVer(private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<AdapterProductVer.ProductViewHolder>() {

    private var products: List<Product> = listOf()

    fun setData(list: List<Product>?){
        if (list != null){
            products = list
            notifyDataSetChanged()
        }
    }

    inner class ProductViewHolder(private val binding: ItemNewLayoutVerBinding,val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val name = binding.nameProduct
        val price = binding.priceProduct
        val linearLayout = binding.layoutItemProduct
        fun bind(currentProduct: Product){
            Glide.with(context)
                .load(currentProduct.images[0].url)
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.loading_img)
                .into(image)
            name.text = currentProduct.product_name.toString()
            price.text = "${currentProduct.product_price} đ"

            linearLayout.setOnClickListener {
                onClickItem(currentProduct._id)
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
        return products.size
    }


}
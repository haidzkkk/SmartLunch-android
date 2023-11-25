package com.fpoly.smartlunch.ui.main.love.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemProductFavouriteBinding
import com.fpoly.smartlunch.databinding.ItemProductLayoutHozBinding
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.formatRate
import com.fpoly.smartlunch.ultis.setMargins


class ProductFavouriteAdapter(private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<ProductFavouriteAdapter.ProductViewHolder>() {

    private var products: List<Product> = listOf()

    fun setData(list: List<Product>?){
        if (list != null){
            products = list
            notifyDataSetChanged()
        }
    }
    inner class ProductViewHolder(private val binding: ItemProductFavouriteBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val Liner_hoz = binding.LinerHoz
        val name = binding.nameProduct
        val price = binding.priceProduct
        val tvRate = binding.tvRate
        fun bind(currentProduct : Product, position: Int ) {
            Glide.with(context)
                .load( if(currentProduct.images.isNotEmpty()) currentProduct.images[0].url else "")
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.loading_img)
                .into(image)
            name.text = currentProduct.product_name
            tvRate.text = " ${currentProduct.rate.formatRate()} (${currentProduct.rate_count})"
            price.text = currentProduct.product_price.toDouble().formatCash()
            Liner_hoz.setOnClickListener {
                onClickItem(currentProduct._id)
            }

            if (position % 2 == 0) binding.layoutMain.setMargins(0, 0, 10, 0)
            else binding.layoutMain.setMargins(10, 0, 0, 0)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductFavouriteBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding,parent.context)
    }
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct: Product = products[position]
        holder.bind(currentProduct, position)

    }

    override fun getItemCount(): Int {
        return products.size
    }
}
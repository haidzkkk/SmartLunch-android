package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Doc
import com.fpoly.smartlunch.databinding.ItemProductLayoutHozBinding


class AdapterProduct(private val context: Context) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {

    var products: List<Doc> = ArrayList()

    fun setData(list: List<Doc>){
        products = list
        notifyDataSetChanged()
    }
    class ProductViewHolder(private val binding: ItemProductLayoutHozBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val name = binding.nameProduct
        val price = binding.priceProduct

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductLayoutHozBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct: Doc = products[position]
        holder.name.text = currentProduct.product_name.toString()
        holder.price.text = currentProduct.product_price.toString()

    }

    override fun getItemCount(): Int {
        return products.size
    }
}


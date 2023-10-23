package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemNewLayoutVerBinding
import com.fpoly.smartlunch.ui.main.product.ProductActivity

class AdapterProductVer(private val context: Context) : RecyclerView.Adapter<AdapterProductVer.ProductViewHolder>() {

    var products: List<Product> = ArrayList()

    class ProductViewHolder(private val binding: ItemNewLayoutVerBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val name = binding.nameProduct
        val price = binding.priceProduct
        val linearLayout = binding.layoutItemProduct
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewLayoutVerBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val currentProduct: Product = products[position]
            holder.name.text = currentProduct.product_name.toString()
            holder.price.text = currentProduct.product_price.toString()

        holder.linearLayout.setOnClickListener {
            val selectedProduct = products[position]
            val bundle = Bundle()
            bundle.putString("id", selectedProduct._id)
            val intent = Intent(context, ProductActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return products.size
    }


}
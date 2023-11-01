package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Category
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.LayoutCategoryBinding
import com.fpoly.smartlunch.databinding.LayoutListProductBinding

class AdapterListProduct(private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<AdapterListProduct.ProductListViewHolder>() {

    var currentProduct: List<Product> = ArrayList()

    fun setData(list: List<Product>?){
        if (list != null){
            currentProduct = list
            notifyDataSetChanged()
        }

    }
    inner class ProductListViewHolder(private val binding: LayoutListProductBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val imageCategory = binding.image
        val nameCategory = binding.nameProductListDetail
        val linerCategory = binding.layoutItemProduct
        fun bind(product : Product){
            Glide.with(context)
                .load(product.images[0].url)
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.loading_img)
                .into(imageCategory)
            nameCategory.text = product.product_name
            linerCategory.setOnClickListener {
                onClickItem(product._id)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutListProductBinding.inflate(inflater, parent, false)
        return ProductListViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val currentProduct: Product = currentProduct[position]
        holder.bind(currentProduct)

    }
    override fun getItemCount(): Int {
        return currentProduct.size
    }

}

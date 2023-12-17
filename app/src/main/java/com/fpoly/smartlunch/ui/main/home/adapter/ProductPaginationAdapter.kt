package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemLoadMoreBinding
import com.fpoly.smartlunch.databinding.ItemNewLayoutVerBinding
import com.fpoly.smartlunch.databinding.ItemProductBinding
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.formatRate
import com.fpoly.smartlunch.ultis.formatView

class ProductPaginationAdapter(private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private object Type{
        const val loading = 0
        const val item = 1
    }

    public var isLoadingOk = false
    public var isLastPage = false
    public var curentPage = 1

    private var products: ArrayList<Product> = arrayListOf()
    private var isLoading: Boolean = false

    fun setData(list: ArrayList<Product>?){
        removeFooterLoading()
        if (list.isNullOrEmpty()){
            isLastPage = true
            return
        }
        products.addAll(list)
        addFooterLoading()
        notifyDataSetChanged()
    }

    fun resetData(){
        this.products.clear()
        isLoadingOk = false
        isLastPage = false
        curentPage = 1
        isLoading = false

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == products.size - 1 && isLoading) return Type.loading
        return return Type.item
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    )
    = if (viewType == Type.loading) LoadMoreViewHolder(ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else ProductViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ProductViewHolder ->{
                val currentProduct: Product = products[position]
                holder.bind(currentProduct)
            }
            is LoadMoreViewHolder ->{
                holder.bind()
            }
        }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentProduct: Product){
            binding.apply {
                Glide.with(root.context)
                    .load( if(currentProduct.images.isNotEmpty()) currentProduct.images[0].url else "")
                    .placeholder(R.drawable.loading_img)
                    .error(R.drawable.loading_img)
                    .into(image)

                nameProduct.text = currentProduct.product_name
                priceProduct.text = currentProduct.product_price.formatCash()
                tvRate.text = " ${currentProduct.rate.formatRate()} "+root.context.getString(R.string.comment)
                tvBuy.text = root.context.getString(R.string.sold)+" ${currentProduct.bought.formatView()}"

                layoutItemProduct.setOnClickListener {
                    onClickItem(currentProduct._id)
                }
            }
        }
    }

    inner class LoadMoreViewHolder(private val binding: ItemLoadMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(){
        }
    }

    public fun addFooterLoading(){
        isLoading = true
        val position = products.size - 1
        if (position >= 0){
            products.add(products[position])
        }
    }
    public fun removeFooterLoading(){
        isLoading = false
        val position = products.size - 1
        if (position >= 0){
            products.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
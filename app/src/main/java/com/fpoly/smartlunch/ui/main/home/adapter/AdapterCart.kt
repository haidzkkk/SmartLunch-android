package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.activityViewModel
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.ItemCartBinding
import com.fpoly.smartlunch.ui.main.product.ProductViewModel

interface ItemTouchHelperAdapter {
    fun onItemSwiped(position: Int)
}

class AdapterCart(
    private val onClickItem: (id: String, purchaseQuantity: Int, sizeId: String) -> Unit,
    private val onSwipeItem: (id: String, purchaseQuantity: Int?, sizeId: String) -> Unit
) :
    RecyclerView.Adapter<AdapterCart.CartViewHolder>(), ItemTouchHelperAdapter {
    private var currentSoldQuantity: Int = 0
    private var productsCart: List<ProductCart> = listOf()
    fun setData(list: List<ProductCart>?) {
        if (list != null) {
            productsCart = list
            notifyDataSetChanged()
        }
    }

    inner class CartViewHolder(private val binding: ItemCartBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        val image = binding.image
        val name = binding.nameProductSheet
        val price = binding.priceProductSheet
        val quanlity = binding.someIdQuanlitySheet
        val quantily_tru = binding.linearMinu1Sheet
        val quantily_cong = binding.linearMinu2Sheet

        fun bind(currentProduct: ProductCart) {
            Glide.with(context).load(currentProduct.image).placeholder(R.drawable.loading_img)
                .into(image)
            name.text = currentProduct.product_name.toString()
            price.text = currentProduct.product_price.toString()
            quanlity.text = currentProduct.purchase_quantity.toString()
            currentSoldQuantity = currentProduct.purchase_quantity

            binding.image.setOnClickListener {
                onClickItem(currentProduct.productId, currentSoldQuantity, currentProduct.sizeId)
            }

            quantily_cong.setOnClickListener {
                currentSoldQuantity++
                quanlity.text = currentSoldQuantity.toString()
                onClickItem(currentProduct.productId, currentSoldQuantity, currentProduct.sizeId)
            }

            quantily_tru.setOnClickListener {
                if (currentSoldQuantity > 1) {
                    currentSoldQuantity--
                    quanlity.text = currentSoldQuantity.toString()
                    onClickItem(
                        currentProduct.productId,
                        currentSoldQuantity,
                        currentProduct.sizeId
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentProduct: ProductCart = productsCart[position]
        holder.bind(currentProduct)

    }

    override fun getItemCount(): Int {
        return productsCart.size
    }

    override fun onItemSwiped(position: Int) {
        val currentProduct = productsCart[position]
        onSwipeItem(
            currentProduct.productId,
            currentSoldQuantity,
            currentProduct.sizeId
        )
    }

}

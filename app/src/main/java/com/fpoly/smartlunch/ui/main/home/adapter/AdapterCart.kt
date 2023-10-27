package com.fpoly.smartlunch.ui.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.activityViewModel
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.ItemCartBinding
import com.fpoly.smartlunch.ui.main.product.ProductViewModel


class AdapterCart(private val onClickItem: (id: String, purchaseQuantity: Int, sizeId: String) -> Unit) :
    RecyclerView.Adapter<AdapterCart.CartViewHolder>() {

    private var productsCart: List<ProductCart> = listOf()

    fun setData(list: List<ProductCart>?) {
        if (list != null) {
            productsCart = list
            notifyDataSetChanged()
        }
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val name = binding.nameProductSheet
        val price = binding.priceProductSheet
        val quanlity = binding.someIdQuanlitySheet
        val quantily_tru = binding.linearMinu1Sheet
        val quantily_cong = binding.linearMinu2Sheet
        fun bind(currentProduct: ProductCart) {
            name.text = currentProduct.product_name.toString()
            price.text = currentProduct.product_price.toString()
            quanlity.text = currentProduct.purchase_quantity.toString()

            var currentSoldQuantity: Int = currentProduct.purchase_quantity
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
            onClickItem(currentProduct.productId, currentSoldQuantity, currentProduct.sizeId)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentProduct: ProductCart = productsCart[position]
        holder.bind(currentProduct)

    }


    override fun getItemCount(): Int {
        return productsCart.size
    }

}

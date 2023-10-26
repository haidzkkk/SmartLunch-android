package com.fpoly.smartlunch.ui.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.ItemCartBinding


class AdapterCart (private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<AdapterCart.CartViewHolder>() {

    var productsCart: List<ProductCart> = ArrayList()
    private  var currentSoldQuantity: Int? = null

    class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val name = binding.nameProductSheet
        val price = binding.priceProductSheet
        val linearLayout = binding.RelaCard
        val quanlity = binding.someIdQuanlitySheet
        val quantily_tru = binding.linearMinu1Sheet
        val quantily_cong = binding.linearMinu2Sheet

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
        holder.name.text = currentProduct.product_name.toString()
        holder.price.text = currentProduct.product_price.toString()
        holder.quanlity.text = currentProduct.purchase_quantity.toString()

        var currentSoldQuantity: Int = currentProduct.purchase_quantity

        holder.quantily_cong.setOnClickListener {
            // Increment the quantity when the button is clicked
            currentSoldQuantity++
            holder.quanlity.text = currentSoldQuantity.toString()
        }

        holder.quantily_tru.setOnClickListener {
            // Decrement the quantity if it's greater than 1
            if (currentSoldQuantity > 1) {
                currentSoldQuantity--
                holder.quanlity.text = currentSoldQuantity.toString()
            }
        }
    }
    override fun getItemCount(): Int {
        return productsCart.size
    }

}

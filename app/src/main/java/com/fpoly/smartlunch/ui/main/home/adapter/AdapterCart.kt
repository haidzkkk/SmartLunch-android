package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.activityViewModel
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.ItemCartBinding
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.formatCash

interface ItemTouchHelperAdapter {
    fun onItemSwiped(position: Int)
}

@SuppressLint("NotifyDataSetChanged")
class AdapterCart(
    private val onClickLisstenner: ItemClickLisstenner
) : RecyclerView.Adapter<AdapterCart.CartViewHolder>(), ItemTouchHelperAdapter {

    private var listProductCart: ArrayList<ProductCart> = arrayListOf()
    fun setData(list: ArrayList<ProductCart>?) {
        listProductCart = list ?: arrayListOf()
        notifyDataSetChanged()
    }

    fun changeData(productCart: ProductCart?){
        if (productCart != null) return

        val indexFind = listProductCart.indexOfFirst { it._id == productCart?._id }
        if (indexFind != -1) {
            listProductCart[indexFind] = productCart!!
            notifyItemChanged(indexFind)
        }
    }

    inner class CartViewHolder(private val binding: ItemCartBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        val image = binding.image
        val name = binding.tvName
        val price = binding.tvPrice
        val quanlity = binding.tvQuantity
        val tvSize = binding.tvSize
        val quantily_tru = binding.linearMinu1Sheet
        val quantily_cong = binding.linearMinu2Sheet

        fun bind(currentProduct: ProductCart) {
            Glide.with(context).load(currentProduct.productId.images[0].url).placeholder(R.drawable.loading_img)
                .into(image)
            name.text = currentProduct.productId.product_name
            price.text = currentProduct.sizeId.size_price.formatCash()
            quanlity.text = currentProduct.purchase_quantity.toString()
            tvSize.text = currentProduct.sizeId.size_name
            var currentSoldQuantity = currentProduct.purchase_quantity

            binding.root.setOnClickListener {
                onClickLisstenner.onClickItem(currentProduct.productId._id)
            }

            quantily_cong.setOnClickListener {
                currentSoldQuantity++
                quanlity.text = currentSoldQuantity.toString()
                onClickLisstenner.onChangeQuantity(currentProduct.productId._id, currentSoldQuantity, currentProduct.sizeId._id)
            }

            quantily_tru.setOnClickListener {
                if (currentSoldQuantity > 1) {
                    currentSoldQuantity--
                    quanlity.text = currentSoldQuantity.toString()
                    onClickLisstenner.onChangeQuantity(currentProduct.productId._id, currentSoldQuantity, currentProduct.sizeId._id)
                }
            }

            binding.layoutIsActive.isVisible = !currentProduct.productId.isActive
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
        val currentProduct: ProductCart = listProductCart[position]
        holder.bind(currentProduct)

    }

    override fun getItemCount(): Int {
        return listProductCart.size
    }

    override fun onItemSwiped(position: Int) {
        val currentProduct = listProductCart[position]
        onClickLisstenner.onSwipeItem(
            currentProduct.productId._id,
            currentProduct.purchase_quantity,
            currentProduct.sizeId._id
        )
    }

    abstract class ItemClickLisstenner() {
        open fun onClickItem(idProductAdapter: String) {}
        open fun onChangeQuantity(idProductAdapter: String, currentSoldQuantity: Int, currentSizeID: String) {}
        open fun onSwipeItem(idProductAdapter: String, currentSoldQuantity: Int?, currentSizeID: String) {}

    }
}

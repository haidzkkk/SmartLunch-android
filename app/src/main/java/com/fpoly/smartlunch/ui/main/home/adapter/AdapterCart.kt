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
    private val onClickLisstenner: ItemClickLisstenner
) :
    RecyclerView.Adapter<AdapterCart.CartViewHolder>(), ItemTouchHelperAdapter {
    private var listProductCart: ArrayList<ProductCart> = arrayListOf()
    fun setData(list: ArrayList<ProductCart>?) {
        if (list != null) {
            listProductCart = list
            notifyDataSetChanged()
        }
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
            var currentSoldQuantity = currentProduct.purchase_quantity

            binding.image.setOnClickListener {
                onClickLisstenner.onClickItem(currentProduct.productId, currentSoldQuantity, currentProduct.sizeId)
            }

            quantily_cong.setOnClickListener {
                currentSoldQuantity++
                quanlity.text = currentSoldQuantity.toString()
                onClickLisstenner.onChangeQuantity(currentProduct.productId, currentSoldQuantity, currentProduct.sizeId)
            }

            quantily_tru.setOnClickListener {
                if (currentSoldQuantity > 1) {
                    currentSoldQuantity--
                    quanlity.text = currentSoldQuantity.toString()
                    onClickLisstenner.onChangeQuantity(currentProduct.productId, currentSoldQuantity, currentProduct.sizeId)
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
        val currentProduct: ProductCart = listProductCart[position]
        holder.bind(currentProduct)

    }

    override fun getItemCount(): Int {
        return listProductCart.size
    }

    override fun onItemSwiped(position: Int) {
        val currentProduct = listProductCart[position]
        onClickLisstenner.onSwipeItem(
            currentProduct.productId,
            currentProduct.purchase_quantity,
            currentProduct.sizeId
        )
    }

    abstract class ItemClickLisstenner() {
        open fun onClickItem(idProductAdapter: String, currentSoldQuantity: Int, currentSizeID: String) {}
        open fun onChangeQuantity(idProductAdapter: String, currentSoldQuantity: Int, currentSizeID: String) {}
        open fun onSwipeItem(idProductAdapter: String, currentSoldQuantity: Int?, currentSizeID: String) {}

    }
}

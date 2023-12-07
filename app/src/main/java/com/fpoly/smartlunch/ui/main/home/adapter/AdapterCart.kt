package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.activityViewModel
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.Topping
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

    inner class CartViewHolder(val binding: ItemCartBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentProduct: ProductCart) {
            with(binding){
                Glide.with(context).load(currentProduct.productId.images[0].url).placeholder(R.drawable.loading_img)
                    .into(image)
                tvName.text = currentProduct.productId.product_name
                tvPrice.text = currentProduct.sizeId.size_price.formatCash()
                tvQuantity.text = currentProduct.purchase_quantity.toString()
                tvSize.text = currentProduct.sizeId.size_name
                var currentSoldQuantity = currentProduct.purchase_quantity

                binding.root.setOnClickListener {
                    onClickLisstenner.onClickItem(currentProduct.productId._id)
                }

                btnCong.setOnClickListener {
                    currentSoldQuantity++
                    tvQuantity.text = currentSoldQuantity.toString()
                    onClickLisstenner.onChangeQuantity(currentProduct.productId._id, currentSoldQuantity, currentProduct.sizeId._id, null)
                }

                btnTru.setOnClickListener {
                    if (currentSoldQuantity > 1) {
                        currentSoldQuantity--
                        tvQuantity.text = currentSoldQuantity.toString()
                        onClickLisstenner.onChangeQuantity(currentProduct.productId._id, currentSoldQuantity, currentProduct.sizeId._id, null)
                    }
                }

                var toppingAdapter = ToppingAdapter(ToppingAdapter.TYPE_ITEM_SMALL, object : ToppingAdapter.OnItenClickLisstenner{
                    override fun onItemClick(topping: Topping) {
                    }

                    override fun onChangeQuantity(topping: Topping) {
                        onClickLisstenner.onChangeQuantity(currentProduct.productId._id, topping.quantity, currentProduct.sizeId._id, topping._id)
                    }
                })


                toppingAdapter.setData(currentProduct.toppings.map { Topping.toTopping(it) })
                rcvToping.adapter = toppingAdapter

                tvTitleTopping.isVisible = currentProduct.toppings.isNotEmpty()
                if (!currentProduct.productId.isActive){
                    binding.root.foreground = ContextCompat.getDrawable(binding.root.context, R.drawable.background_transparent)
                }else{
                    binding.root.foreground = null
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
            currentProduct.productId._id,
            currentProduct.purchase_quantity,
            currentProduct.sizeId._id
        )
    }

    abstract class ItemClickLisstenner() {
        open fun onClickItem(idProductAdapter: String) {}
        open fun onChangeQuantity(idProductAdapter: String,
                                  currentSoldQuantity: Int,
                                  currentSizeID: String,
                                  toppingId: String?
        ) {}
        open fun onSwipeItem(idProductAdapter: String, currentSoldQuantity: Int?, currentSizeID: String) {}

    }
}

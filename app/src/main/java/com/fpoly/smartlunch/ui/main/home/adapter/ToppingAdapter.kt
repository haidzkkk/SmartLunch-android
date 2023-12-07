package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Topping
import com.fpoly.smartlunch.databinding.ItemToppingBinding
import com.fpoly.smartlunch.databinding.ItemToppingSmallBinding
import com.fpoly.smartlunch.databinding.ItemToppingViewBinding
import com.fpoly.smartlunch.ultis.formatCash

@SuppressLint("NotifyDataSetChanged")
class ToppingAdapter(private val typeItem: Int,
                     private val onClick: OnItenClickLisstenner
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val TYPE_ITEM_SMALL: Int = 0
        const val TYPE_ITEM_MEDIUM: Int = 1
        const val TYPE_ITEM_VIEW: Int = 2
    }

    interface OnItenClickLisstenner{
        fun onItemClick(topping: Topping)
        fun onChangeQuantity(topping: Topping)
    }

    var toppings: ArrayList<Topping>? = null

    fun setData(data: List<Topping>?) {
        if (data == null) return
        toppings = data as ArrayList<Topping>
        notifyDataSetChanged()
    }

    fun getToppingsSelect(): List<Topping>? = toppings?.filter { it.quantity > 0 }

    fun getTotalPriceTopping(): Double {
        var totalPrice: Double = 0.0
        toppings?.forEach{
            totalPrice += (it.price * it.quantity)
        }
        return totalPrice
    }
    override fun getItemCount(): Int {
        if (toppings != null) return toppings!!.size
        return 0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (typeItem == TYPE_ITEM_SMALL) ViewHolderSmall(ItemToppingSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else if (typeItem == TYPE_ITEM_MEDIUM) ViewHolderMedium(ItemToppingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else ViewHolderView(ItemToppingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolderSmall -> holder.onBind(toppings!![position])
            is ViewHolderMedium -> holder.onBind(toppings!![position])
            is ViewHolderView -> holder.onBind(toppings!![position])
        }
    }

    inner class ViewHolderSmall(val binding: ItemToppingSmallBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(topping: Topping){

            binding.tvName.text = topping.name
            binding.tvPrice.text = topping.price.formatCash()
            binding.tvQuantity.text = topping.quantity.toString()
            binding.viewActive.isVisible = !topping.isActive
            binding.root.setOnClickListener{
                onClick.onItemClick(topping)
            }
            binding.btnPlus.setOnClickListener{
                topping.plusQuantity()
                onClick.onChangeQuantity(topping)
                notifyDataSetChanged()
            }
            binding.btnMinus.setOnClickListener{
                topping.minusQuantity()
                onClick.onChangeQuantity(topping)
                notifyDataSetChanged()
            }
        }
    }
    inner class ViewHolderMedium(val binding: ItemToppingBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(topping: Topping){

            binding.tvName.text = topping.name
            binding.tvPrice.text = topping.price.formatCash()
            binding.tvQuantity.text = topping.quantity.toString()

            binding.root.setOnClickListener{
                onClick.onItemClick(topping)
            }
            binding.btnPlus.setOnClickListener{
                topping.plusQuantity()
                onClick.onItemClick(topping)
                notifyDataSetChanged()
            }
            binding.btnMinus.setOnClickListener{
                topping.minusQuantity()
                onClick.onItemClick(topping)
                notifyDataSetChanged()
            }
        }
    }
    inner class ViewHolderView(val binding: ItemToppingViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(topping: Topping){

            binding.tvName.text = topping.name
            binding.tvPrice.text = topping.price.formatCash()
            binding.tvQuantity.text = "x${topping.quantity}"

            binding.root.setOnClickListener{
                onClick.onItemClick(topping)
            }
        }
    }
}
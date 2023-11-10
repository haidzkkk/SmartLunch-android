package com.fpoly.smartlunch.ui.payment.payment

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.databinding.ItemBottomPaymentTypeBinding

@SuppressLint("NotifyDataSetChanged")
class PaymentTypeAdapter(val callBack: (menu: Menu) -> Unit) : RecyclerView.Adapter<PaymentTypeAdapter.ViewHolder>() {

    private var positionItemSelect = 0
    private var listMenu = arrayListOf<Menu>()

    fun setData(list: ArrayList<Menu>){
        this.listMenu = list
        notifyDataSetChanged()
    }

    fun setSelectItem(menu: Menu?){
        val index = listMenu.indexOf(menu)
        if (index >= 0) positionItemSelect = index
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemBottomPaymentTypeBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(menu: Menu, position: Int){
            Log.e("TAG", "onBind: $positionItemSelect", )
            binding.rdoType.isChecked = position == positionItemSelect
            binding.rdoType.text = menu.name
            binding.tvDesc.text = menu.desc

            binding.root.setOnClickListener{
                callBack(menu)
            }

            binding.rdoType.setOnClickListener{
                callBack(menu)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBottomPaymentTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listMenu.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listMenu[position], position)
    }
}
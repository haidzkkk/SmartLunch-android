package com.fpoly.smartlunch.ui.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemCouponsBinding

class AdapterCoupons (private val onClickItem :(id: String) -> Unit) : RecyclerView.Adapter<AdapterCoupons.CouponsViewHolder>() {

    var listCoupons: List<CouponsResponse> = listOf()
    fun setData(list: List<CouponsResponse>?){
        if (list != null){
            listCoupons = list
            notifyDataSetChanged()
        }
    }
    class CouponsViewHolder(private val binding: ItemCouponsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val date = binding.tvDate
        val free = binding.tvFree
        val ship = binding.tvFreeShip
        val linner = binding.linerCoupon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponsBinding.inflate(inflater, parent, false)
        return CouponsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponsViewHolder, position: Int) {
        val coupons: CouponsResponse = listCoupons[position]
        holder.date.text = coupons.expiration_date
        holder.free.text = "FREE"
        holder.ship.text = "Freeship tới 3km"
        updateUI(holder, coupons.isSelected)
        holder.linner.setOnClickListener{
            listCoupons.forEach { it.isSelected = false }
            coupons.isSelected = true
            onClickItem(coupons._id)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return listCoupons.size
    }

    private fun updateUI(holder: CouponsViewHolder, isSelected: Boolean) {
        if (isSelected) {
            holder.linner.setBackgroundResource(R.drawable.khung)
            holder.free.setTextColor(Color.RED)
        } else {
            holder.free.setTextColor(Color.BLACK)
            holder.linner.setBackgroundResource(R.drawable.khung1)
        }
    }
}
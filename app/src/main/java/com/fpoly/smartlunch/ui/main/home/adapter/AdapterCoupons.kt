package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.ItemCouponsBinding
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.formatCash

@SuppressLint("NotifyDataSetChanged")
class AdapterCoupons (private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<AdapterCoupons.CouponsViewHolder>() {

    var couponSelect: CouponsResponse? = null
    var listCoupons: List<CouponsResponse> = listOf()
    fun setData(list: List<CouponsResponse>?){
        if (list != null){
            listCoupons = list
            notifyDataSetChanged()
        }
    }

    fun selectItem(couponsResponse: CouponsResponse?){
        if (couponsResponse == null ){
            Log.e("AdapterCoupons", "selectItem: ${couponsResponse}", )
            couponSelect = null
            notifyDataSetChanged()
        }else{
            val position = listCoupons.indexOfFirst { it._id == couponsResponse._id }
            if (position == -1) {
                return
            }else{
                couponSelect = couponsResponse
                notifyDataSetChanged()
            }
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
        holder.date.text = coupons.expiration_date.convertIsoToStringFormat(StringUltis.dateDayFormat)
        holder.free.text = "${coupons.coupon_name} ${coupons.discount_amount}%"
        holder.ship.text = "Tối thiểu ${coupons.min_purchase_amount.toDouble().formatCash()}"

        if (coupons == couponSelect) {
            holder.linner.setBackgroundResource(R.drawable.khung)
            holder.free.setTextColor(Color.RED)
        } else {
            holder.free.setTextColor(Color.BLACK)
            holder.linner.setBackgroundResource(R.drawable.khung1)
        }

        holder.linner.setOnClickListener{
            couponSelect = if (couponSelect == coupons) null else coupons
            notifyDataSetChanged()
            onClickItem(coupons._id)
        }

    }

    override fun getItemCount(): Int {
        return listCoupons.size
    }
}
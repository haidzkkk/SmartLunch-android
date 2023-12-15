package com.fpoly.smartlunch.ui.main.coupons.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.databinding.ItemCouponBinding
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.formatCash

class CouponsAdapter(private val onClickItem: (String) -> Unit) : RecyclerView.Adapter<CouponsAdapter.CouponViewHolder>() {
    private var couponsList: List<CouponsResponse> = listOf()
    fun setData(list: List<CouponsResponse>?){
        if (list!=null){
            couponsList=list
            notifyDataSetChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val currentCoupon = couponsList[position]
        holder.bind(currentCoupon)
    }

    override fun getItemCount() = couponsList.size

    inner class CouponViewHolder(private val binding: ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(coupon: CouponsResponse) {
            binding.apply {
                tvName.text = coupon.coupon_name
                tvCode.text = "${binding.root.context.getString(R.string.minimum)} ${coupon.min_purchase_amount.toDouble().formatCash()}"
                date.text = " ${coupon.expiration_date.convertIsoToStringFormat(StringUltis.dateDayFormat)}"
                Glide.with(root.context).load(if(coupon.coupon_images.isNotEmpty()) coupon.coupon_images[0].url else "").into(imgCoupon)
            }
            binding.root.setOnClickListener {
                onClickItem(coupon._id)
            }
        }
    }
}

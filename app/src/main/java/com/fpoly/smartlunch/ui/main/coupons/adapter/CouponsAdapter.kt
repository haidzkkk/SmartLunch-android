package com.fpoly.smartlunch.ui.main.coupons.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.databinding.ItemCouponBinding

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

        fun bind(coupon: CouponsResponse) {
            binding.apply {
                couponName.text = coupon.coupon_name
                couponCode.text = coupon.coupon_code
                date.text=coupon.expiration_date
            }
            binding.root.setOnClickListener {
                onClickItem(coupon._id)
            }
        }
    }
}

package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.Image
import com.fpoly.smartlunch.databinding.ItemBannerBinding
import com.fpoly.smartlunch.ultis.getListBanner

@SuppressLint("NotifyDataSetChanged")
class BannerAdapter(val onClick: (banner: Banner) -> Unit) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    var list: ArrayList<Banner> = arrayListOf()
    fun setData(list: ArrayList<Banner>?){
        if (list.isNullOrEmpty()){
            this.list = getListBanner()
        }else{
            this.list.clear()
            this.list.addAll(list)
        }
        notifyDataSetChanged()
    }
    inner class ViewHolder(private val binding: ItemBannerBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(banner: Banner){
            Glide.with(binding.root.context).load(banner.img.url).placeholder(R.drawable.loading_img).into(binding.imgBanner)

            binding.root.setOnClickListener{
                onClick(banner)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        if (list.isNotEmpty()) return list.size
        return 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isNotEmpty())
            holder.onBind(list[position])
        else holder.onBind(Banner("sad", 0, Image("a", "a"), "sadsa"))
    }
}
package com.fpoly.smartlunch.ui.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.Image
import com.fpoly.smartlunch.databinding.ItemBannerBinding
import com.fpoly.smartlunch.ultis.getListBanner
import com.stfalcon.imageviewer.StfalconImageViewer

class ImageSlideAdapter(private val dataItemSelect: (string: String) -> Unit) : RecyclerView.Adapter<ImageSlideAdapter.ViewHolder>() {

    var list: List<Image> = listOf()
    fun setData(list: List<Image>?){
        if (list.isNullOrEmpty()) return

        this.list = list
        notifyDataSetChanged()
    }
    inner class ViewHolder(private val binding: ItemBannerBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(image: Image, position: Int){
            Glide.with(binding.root.context).load(image.url).placeholder(R.drawable.loading_img).into(binding.imgBanner)

            dataItemSelect("${position + 1}/$itemCount")

            binding.root.setOnClickListener{
                StfalconImageViewer.Builder<Image>(binding.root.context, list) { view, image ->
                    Glide.with(view).load(image.url).into(view)
                }
                    .withStartPosition(position)
                    .withBackgroundColorResource(R.color.black)
                    .withHiddenStatusBar(true)
                    .show()
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
            holder.onBind(list[position], position)
        else holder.onBind(Image("a", "a"), position)
    }
}
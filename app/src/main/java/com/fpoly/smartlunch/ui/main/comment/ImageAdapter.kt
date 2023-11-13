package com.fpoly.smartlunch.ui.main.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Image
import com.fpoly.smartlunch.databinding.ItemGalleryChatBinding
import com.fpoly.smartlunch.databinding.ItemImageBinding
import com.stfalcon.imageviewer.StfalconImageViewer

class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    var listImage: ArrayList<Image> = arrayListOf()

    fun setData(data: ArrayList<Image>?){
        if (data == null) return
        this.listImage = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(image: Image, position: Int){
            Glide.with(binding.root.context)
                .load(image.url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .signature(ObjectKey(System.currentTimeMillis()))
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.imgBody)

            binding.imgBody.setOnClickListener{
                StfalconImageViewer.Builder<Image>(binding.root.context, listImage) { view, image ->
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
       return ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listImage.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listImage.get(position), position)
    }

}
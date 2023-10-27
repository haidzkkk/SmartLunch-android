package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.data.model.Category
import com.fpoly.smartlunch.databinding.LayoutCategoryBinding

class AdapterCategory (private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {

    var categories: List<Category> = ArrayList()
   inner class CategoryViewHolder( val binding : LayoutCategoryBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val imageCategory = binding.imageCategory
        val nameCategory = binding.nameCategory
        val linerCategory = binding.LinerCategory
        fun bind(category : Category ){
            Glide.with(context)
                .load(category.category_image) // Đặt URL hình ảnh vào load()
                .into(imageCategory)
            nameCategory.text = category.category_name.toString()

            linerCategory.setOnClickListener {
                onClickItem(category._id)

            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutCategoryBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentCategory: Category = categories[position]
         holder.bind(currentCategory)

    }
    override fun getItemCount(): Int {
        return categories.size
    }

}

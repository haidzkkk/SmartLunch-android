package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Category
import com.fpoly.smartlunch.databinding.ItemCategoryBinding

class CategoryOutsideAdapter(private val onClickItem: (id: String) -> Unit) : RecyclerView.Adapter<CategoryOutsideAdapter.CategoryOutsideViewHolder>() {

    private var categories: ArrayList<Category> = ArrayList()
    fun setData(list: List<Category>?) {
        if (list != null) {
            categories.clear()
            val limit = minOf(3, list.size)

            for (i in 0 until limit) {
                categories.add(list[i])
            }
            notifyDataSetChanged()
        }
    }

    inner class CategoryOutsideViewHolder(val binding : ItemCategoryBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val imageCategory = binding.imageCategory
        val nameCategory = binding.categoryName
        fun bind(category : Category){
            Glide.with(context)
                .load(category.category_image.url)
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.loading_img)
                .into(imageCategory)
            nameCategory.text = category.category_name

            binding.root.setOnClickListener {
                onClickItem(category._id)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryOutsideViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return CategoryOutsideViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CategoryOutsideViewHolder, position: Int) {
        val currentCategory: Category = categories[position]
        holder.bind(currentCategory)

    }
    override fun getItemCount(): Int {
        return categories.size
    }

}
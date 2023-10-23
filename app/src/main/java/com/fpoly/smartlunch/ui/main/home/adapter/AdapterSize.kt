package com.fpoly.smartlunch.ui.main.home.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.databinding.ItemProductLayoutHozBinding
import com.fpoly.smartlunch.databinding.LayoutSizeBinding
import com.fpoly.smartlunch.ui.main.product.ProductActivity

class AdapterSize(private val context: Context) : RecyclerView.Adapter<AdapterSize.SizeViewHolder>() {

    var Listsize: List<Size> = ArrayList()


    class SizeViewHolder(private val binding: LayoutSizeBinding) : RecyclerView.ViewHolder(binding.root) {
        val size = binding.NameSize
        val liner = binding.linerSize


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutSizeBinding.inflate(inflater, parent, false)
        return SizeViewHolder(binding)
    }
    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val currentProduct: Size = Listsize[position]
        holder.size.text = currentProduct.size_name
        updateUI(holder, currentProduct.isSelected)
        holder.liner.setOnClickListener{
            Listsize.forEach { it.isSelected = false }
            currentProduct.isSelected = true
            notifyDataSetChanged()
            val bundle = Bundle()
            bundle.putString("id_size", currentProduct._id)
            val intent = Intent(context, ProductActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return Listsize.size
    }
    private fun updateUI(holder: SizeViewHolder, isSelected: Boolean) {
        if (isSelected) {
            // Set the selected background and text color
            holder.size.setBackgroundResource(R.drawable.chips)
            holder.size.setTextColor(Color.RED) // Thay đổi màu văn bản
        } else {
            // Set the default background and text color
            holder.size.setBackgroundResource(R.drawable.chips1)
            holder.size.setTextColor(Color.BLACK) // Màu văn bản mặc định
        }
    }
}
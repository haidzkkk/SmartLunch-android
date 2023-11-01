package com.fpoly.smartlunch.ui.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.databinding.LayoutSizeBinding

class AdapterSize(private val onClickItem: (id: String) -> Unit) :
    RecyclerView.Adapter<AdapterSize.SizeViewHolder>() {

    private var listSize: List<Size> = ArrayList()

    fun setData(list: List<Size>?) {
        if (list != null) {
            listSize = list
            notifyDataSetChanged()
        }
    }

    class SizeViewHolder(private val binding: LayoutSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val size = binding.NameSize
        val liner = binding.linerSize
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutSizeBinding.inflate(inflater, parent, false)
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val currentProduct: Size = listSize[position]
        holder.size.text = currentProduct.size_name
        updateUI(holder, currentProduct.isSelected)
        holder.liner.setOnClickListener {
            listSize.forEach { it.isSelected = false }
            currentProduct.isSelected = true
            onClickItem(currentProduct._id)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return listSize.size
    }

    private fun updateUI(holder: SizeViewHolder, isSelected: Boolean) {
        if (isSelected) {
            holder.size.setBackgroundResource(R.drawable.chips)
            holder.size.setTextColor(Color.RED)
        } else {
            holder.size.setBackgroundResource(R.drawable.chips1)
            holder.size.setTextColor(Color.BLACK)
        }
    }

}
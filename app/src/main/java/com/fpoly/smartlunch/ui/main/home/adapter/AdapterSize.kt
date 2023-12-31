package com.fpoly.smartlunch.ui.main.home.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.databinding.LayoutSizeBinding

@SuppressLint("NotifyDataSetChanged")
class AdapterSize(private val onClickItem: (size: Size) -> Unit):
    RecyclerView.Adapter<AdapterSize.SizeViewHolder>() {

    private var listSize: List<Size> = ArrayList()

    fun setData(list: List<Size>?) {
        if (list.isNullOrEmpty()) return
        listSize = list
        notifyDataSetChanged()
    }

    fun setSelect(size: Size?){
        var curenSize = size

        if(listSize.isNotEmpty()){
            if (curenSize == null ){
                curenSize = listSize[0]
            }
            listSize.forEach{
                it.isSelect = it._id == curenSize._id
            }
            onClickItem(curenSize)
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
        val currentSize: Size = listSize[position]
        holder.size.text = currentSize.size_name

        if (currentSize.isSelect) {
            holder.size.setBackgroundResource(R.drawable.chips)
            holder.size.setTextColor(Color.RED)
        } else {
            holder.size.setBackgroundResource(R.drawable.chips1)
            holder.size.setTextColor(Color.BLACK)
        }

        holder.liner.setOnClickListener {
            setSelect(currentSize)
        }
    }

    override fun getItemCount(): Int {
        return listSize.size
    }

}
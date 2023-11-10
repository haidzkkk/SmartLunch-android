package com.fpoly.smartlunch.ui.main.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Address
import com.fpoly.smartlunch.databinding.ItemAddressBinding

class AddressAdapter(val onClickDetail: (Address) -> Unit, val onClick: (Address) -> Unit,) :
RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private var addresss: List<Address> = listOf()

    fun updateSelectedAddress(selectedAddress: Address) {
        addresss.forEach { address ->
            address.isSelected = address == selectedAddress
        }
        notifyDataSetChanged()
    }

    fun setData(list: List<Address>?) {
        if (list!= null){
            addresss = list
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder =
        AddressViewHolder(
            ItemAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresss[position]
        holder.bind(address)
    }

    override fun getItemCount() = addresss.size

    inner class AddressViewHolder(private val itemAddressBinding: ItemAddressBinding) :
        RecyclerView.ViewHolder(itemAddressBinding.root) {
        fun bind(address: Address) {
            itemAddressBinding.recipientName.text = address.recipientName
            itemAddressBinding.phone.text = address.phoneNumber
            itemAddressBinding.address.text = address.addressLine
            itemAddressBinding.radioButtonAddress.isChecked = address.isSelected

            itemAddressBinding.radioButtonAddress.setOnClickListener {
                onClick(address)
            }
            itemAddressBinding.root.setOnClickListener {
                onClick(address)
            }
            itemAddressBinding.btnDetail.setOnClickListener{
                onClickDetail(address)
            }

        }
    }
}
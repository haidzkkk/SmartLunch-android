package com.fpoly.smartlunch.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentAddressBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.profile.adapter.AddressAdapter
import javax.inject.Inject

class AddressFragment : PolyBaseFragment<FragmentAddressBinding>() {
    private val userViewModel: UserViewModel by activityViewModel()
    private var adapterAddress: AddressAdapter? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        listenEvent()
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupAppBar() {
        views.appBar.apply {
            tvTitleToolbar.text = getString(R.string.address)
            btnBackToolbar.visibility = View.VISIBLE
        }
    }

    private fun setupListAddress(){
        adapterAddress = AddressAdapter({

        }){ address ->
            if (address._id == null){
                Toast.makeText(requireContext(), "Không có id address", Toast.LENGTH_SHORT).show()
            }else{
                userViewModel.handle(UserViewAction.UpdateAddress(address._id))
                sessionManager.let {
                    it.saveAddress(address._id)
                }
                adapterAddress?.updateSelectedAddress(address)
            }
        }
        views.rcyAddress.adapter=adapterAddress
    }



    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddressBinding {
        return FragmentAddressBinding.inflate(inflater, container, false)
    }

    override fun invalidate(): Unit = withState(userViewModel) {
        when (it.asyncListAddress) {
            is Success -> {
                setupListAddress()
                adapterAddress?.setData(it.asyncListAddress.invoke())
            }

            else -> {}
        }
    }

}
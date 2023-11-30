package com.fpoly.smartlunch.ui.payment.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentAddressBinding
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.adapter.AddressAdapter
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import javax.inject.Inject

class AddressPaymentFragment : PolyBaseFragment<FragmentAddressBinding>() {
    private val userViewModel: UserViewModel by activityViewModel()
    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private var adapterAddress: AddressAdapter? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setupAppBar()
        listenEvent()
    }

    private fun initUI() {
        userViewModel.handle(UserViewAction.GetListAddress)
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            userViewModel.handle(UserViewAction.GetListAddress)
        }
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.btnAddAddress.setOnClickListener{
            paymentViewModel.returnAddAddressFragment()
        }
    }

    private fun setupAppBar() {
        views.appBar.apply {
            tvTitleToolbar.text = getString(R.string.address)
            btnBackToolbar.visibility=View.VISIBLE
        }
    }

    private fun setupListAddress(){
        adapterAddress = AddressAdapter(
            {addressDetail ->
                if (addressDetail._id == null){
                    Toast.makeText(requireContext(), "Không có id address", Toast.LENGTH_SHORT).show()
                }else{
                    userViewModel.handle(UserViewAction.GetAddressById(addressDetail._id))
                    paymentViewModel.returnDetailAddressFragment()
                }
            },{addressSelect->
                if (addressSelect._id == null){
                    Toast.makeText(requireContext(), "Không có id address", Toast.LENGTH_SHORT).show()
                }else{
                    userViewModel.handle(UserViewAction.UpdateAddress(addressSelect._id))
                    adapterAddress?.updateSelectedAddress(addressSelect)
                }
            }
        )
        views.rcyAddress.adapter=adapterAddress
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddressBinding {
       return FragmentAddressBinding.inflate(inflater,container,false)
    }

    override fun invalidate():Unit= withState(userViewModel) {
        views.swipeLoading.isRefreshing = it.asyncListAddress is Loading

        when(it.asyncListAddress){
            is Success -> {
                setupListAddress()
                adapterAddress?.setData(it.asyncListAddress.invoke())
            }

            else -> {}
        }
    }

}
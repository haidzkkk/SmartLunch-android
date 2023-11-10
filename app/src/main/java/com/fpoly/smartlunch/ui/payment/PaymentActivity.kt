package com.fpoly.smartlunch.ui.payment

import android.os.Bundle
import androidx.core.view.isVisible
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivityPaymentBinding
import com.fpoly.smartlunch.ui.main.product.ProductState
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewState
import com.fpoly.smartlunch.ultis.addFragmentToBackstack
import javax.inject.Inject

class PaymentActivity : PolyBaseActivity<ActivityPaymentBinding>(), PaymentViewModel.Factory,
    UserViewModel.Factory, ProductViewModel.Factory {

    private val paymentViewModel: PaymentViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private val productViewModel : ProductViewModel by viewModel()

    @Inject
    lateinit var paymentViewModelFactory: PaymentViewModel.Factory

    @Inject
    lateinit var userViewModelFactory: UserViewModel.Factory
    @Inject
    lateinit var productViewModelFactory: ProductViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        listenEvent()

        userViewModel.handle(UserViewAction.GetCurrentUser)
    }

    private fun listenEvent() {
        paymentViewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
    }

    private fun handleEvent(event: PaymentViewEvent) {
        when (event) {
            is PaymentViewEvent.ReturnFragment<*> -> {
                addFragmentToBackstack(R.id.frame_layout, event.fragmentClass)
            }

            is PaymentViewEvent.ReturnFragmentWithArgument<*> -> {
                addFragmentToBackstack(
                    R.id.frame_layout,
                    event.fragmentClass,
                    bundle = event.bundle
                )
            }

            is PaymentViewEvent.ReturnShowLoading -> showLayoutLoading(event.isVisible)
        }
    }

    private fun showLayoutLoading(isVisible: Boolean){
        views.layoutLoading.root.isVisible = isVisible
    }

    override fun getBinding(): ActivityPaymentBinding {
        return ActivityPaymentBinding.inflate(layoutInflater)
    }

    override fun create(initialState: PaymentViewState): PaymentViewModel {
        return paymentViewModelFactory.create(initialState)
    }

    override fun create(initialState: UserViewState): UserViewModel {
        return userViewModelFactory.create(initialState)
    }

    override fun create(initialState: ProductState): ProductViewModel {
        return productViewModelFactory.create(initialState)
    }
}
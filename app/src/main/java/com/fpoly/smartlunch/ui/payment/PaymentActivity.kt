package com.fpoly.smartlunch.ui.payment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivityPaymentBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductState
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewEvent
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewState
import com.fpoly.smartlunch.ui.payment.cart.CartFragment
import com.fpoly.smartlunch.ultis.addFragmentToBackStack
import com.fpoly.smartlunch.ultis.popBackStackAndShowPrevious
import com.fpoly.smartlunch.ui.notification.receiver.MyReceiver
import com.fpoly.smartlunch.ultis.startActivityWithData
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK
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

    val intentFilterCall = IntentFilter(MyReceiver.actionCall)
    private val broadcastReceiverCall = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.extras?.getString("type")
            val idUrl = intent?.extras?.getString("idUrl")
            var intentCall = Intent(applicationContext, ChatActivity::class.java)
            startActivityWithData(intentCall, type, idUrl)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        setupMainLayout()
        initUI()
        listenEvent()
        userViewModel.handle(UserViewAction.GetCurrentUser)
    }

    @SuppressLint("CommitTransaction")
    private fun setupMainLayout() {
        supportFragmentManager.commit {
            add<CartFragment>(R.id.frame_layout).addToBackStack(CartFragment::class.java.simpleName)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiverCall, intentFilterCall)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiverCall)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }

    private fun initUI() {
    }

    private fun listenEvent() {
        paymentViewModel.observeViewEvents { event ->
            when (event) {
                is PaymentViewEvent.ReturnFragment<*> -> {
                    addFragmentToBackStack(
                        R.id.frame_layout,
                        event.fragmentClass,
                        event.fragmentClass.simpleName
                    )
                }

                is PaymentViewEvent.ReturnFragmentWithArgument<*> -> {
                    addFragmentToBackStack(
                        R.id.frame_layout,
                        event.fragmentClass,
                        bundle = event.bundle,
                        tag = event.fragmentClass.simpleName
                    )
                }

                is PaymentViewEvent.ReturnShowLoading -> showLayoutLoading(event.isVisible)
                else -> {}
            }
        }

        productViewModel.observeViewEvents {
            when(it){
                is ProductEvent.ReturnFragment<*> -> { addFragmentToBackStack(R.id.frame_layout, it.fragmentClass, bundle = it.bundle) }
                else -> {}
            }
        }

        userViewModel.observeViewEvents {event ->
            when(event){
                is UserViewEvent.ReturnFragment<*> -> {
                    addFragmentToBackStack(
                        R.id.frame_layout,
                        event.fragmentClass,
                        bundle = event.bundle,
                        tag = event.fragmentClass.simpleName
                    )
                }
                else -> {}
            }
        }
    }


    private fun showLayoutLoading(isVisible: Boolean){
        views.layoutLoading.root.isVisible = isVisible
    }

    override fun onBackPressed() {
        super.onBackPressed()
        popBackStackAndShowPrevious()
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
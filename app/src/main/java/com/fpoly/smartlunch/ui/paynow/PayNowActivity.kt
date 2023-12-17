package com.fpoly.smartlunch.ui.paynow

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.databinding.ActivityPayNowBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.profile.UserViewEvent
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewState
import com.fpoly.smartlunch.ui.notification.receiver.MyReceiver
import com.fpoly.smartlunch.ui.payment.PaymentViewEvent
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewState
import com.fpoly.smartlunch.ui.paynow.cart.CartPayNowFragment
import com.fpoly.smartlunch.ultis.addFragmentToBackStack
import com.fpoly.smartlunch.ultis.popBackStackAndShowPrevious
import com.fpoly.smartlunch.ultis.startActivityWithData
import vn.zalopay.sdk.ZaloPaySDK
import javax.inject.Inject

class PayNowActivity : PolyBaseActivity<ActivityPayNowBinding>(),
    PayNowViewModel.Factory,
    PaymentViewModel.Factory,
    UserViewModel.Factory{

    private val payNowViewModel: PayNowViewModel by viewModel()
    private val paymentViewModel: PaymentViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()

    @Inject
    lateinit var userViewModelFactory: UserViewModel.Factory
    @Inject
    lateinit var paymentViewModelFactory: PaymentViewModel.Factory
    @Inject
    lateinit var paynowViewModelFactory: PayNowViewModel.Factory

    val intentFilterCall = IntentFilter(MyReceiver.actionCall)
    private val broadcastReceiverCall = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.extras?.getString("type")
            val idUrl = intent?.extras?.getString("idUrl")
            val intentCall = Intent(applicationContext, ChatActivity::class.java)
            startActivityWithData(intentCall, type, idUrl)
        }
    }

    override fun getBinding(): ActivityPayNowBinding {
        return ActivityPayNowBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        initUI()
        listenEvent()
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
        supportFragmentManager.commit {
            add<CartPayNowFragment>(R.id.frame_layout).addToBackStack(CartPayNowFragment::class.java.simpleName)
        }

        var cartResponse: CartResponse? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cartResponse = intent?.extras?.getSerializable("data", CartResponse::class.java)
        }else{
            cartResponse = intent?.extras?.getSerializable("data") as CartResponse
        }

        if (cartResponse != null){
            payNowViewModel.handle(PayNowViewAction.CheckUpdateCartLocal(cartResponse))
        }
    }

    private fun listenEvent() {
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
        payNowViewModel.observeViewEvents { event ->
            when (event) {
                is PayNowViewEvent.ReturnFragment<*> -> {
                    addFragmentToBackStack(
                        R.id.frame_layout,
                        event.fragmentClass,
                        event.fragmentClass.simpleName
                    )
                }

                is PayNowViewEvent.ReturnFragmentWithArgument<*> -> {
                    addFragmentToBackStack(
                        R.id.frame_layout,
                        event.fragmentClass,
                        bundle = event.bundle,
                        tag = event.fragmentClass.simpleName
                    )
                }

                is PayNowViewEvent.ReturnShowLoading -> showLayoutLoading(event.isVisible)
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

    override fun create(initialState: PaymentViewState): PaymentViewModel {
        return paymentViewModelFactory.create(initialState)
    }

    override fun create(initialState: UserViewState): UserViewModel {
        return userViewModelFactory.create(initialState)
    }

    override fun create(initialState: PayNowViewState): PayNowViewModel {
        return paynowViewModelFactory.create(initialState)
    }
}
package com.fpoly.smartlunch.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivityMainBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.coupons.CouponsFragment
import com.fpoly.smartlunch.ui.main.order.OrderFragment
import com.fpoly.smartlunch.ui.main.home.HomeFragment
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.HomeViewState
import com.fpoly.smartlunch.ui.main.home.TestViewModel
import com.fpoly.smartlunch.ui.main.home.TestViewModelMvRx
import com.fpoly.smartlunch.ui.main.love.FavouriteFragment
import com.fpoly.smartlunch.ui.payment.payment.PayFragment
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductState
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.ProfileFragment
import com.fpoly.smartlunch.ui.main.profile.UserViewEvent
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewState
import com.fpoly.smartlunch.ui.notification.receiver.MyReceiver
import com.fpoly.smartlunch.ui.security.LoginFragment
import com.fpoly.smartlunch.ui.security.SecurityViewModel
import com.fpoly.smartlunch.ui.security.SecurityViewState
import com.fpoly.smartlunch.ui.security.onboarding.ViewPagerFragment
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.fpoly.smartlunch.ultis.addFragmentToBackStack
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import com.fpoly.smartlunch.ultis.popBackStackAndShowPrevious
import com.fpoly.smartlunch.ultis.showUtilDialogWithCallback
import com.fpoly.smartlunch.ultis.startActivityWithData
import javax.inject.Inject


@SuppressLint("UnspecifiedRegisterReceiverFlag")
class MainActivity : PolyBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory, ProductViewModel.Factory,SecurityViewModel.Factory, UserViewModel.Factory {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    @Inject
    lateinit var productViewModelFactory: ProductViewModel.Factory

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var securityFactory: SecurityViewModel.Factory

    @Inject
    lateinit var userViewModelFactory: UserViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager

    private val userViewModel: UserViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val productViewModel: ProductViewModel by viewModel()
    private val testViewModel: TestViewModel by lazy { viewModelProvider.get(TestViewModel::class.java) }
    private val testViewModelMvRx: TestViewModelMvRx by viewModel()

    val intentFilterNotify = IntentFilter(MyReceiver.actionNotify)
    val intentFilterCall = IntentFilter(MyReceiver.actionCall)

    var doubleClickBack: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        handleViewModel()
        setupMainLayout()
        changeMode(sessionManager.fetchDarkMode())
        changeLanguage(sessionManager.fetchLanguage())
        handleReceiveDataNotify()
        registerReceiver(broadcastReceiverNotify, intentFilterNotify)
    }

    private fun handleViewModel() {
        homeViewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }

        productViewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }

        userViewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }

//        productViewModel.subscribe(this){
//            if (it.asyncUnconfirmed is Success && it.asyncConfirmed is Success && it.asyncDelivering is Success){
//                var position =( it.asyncUnconfirmed.invoke()?.size ?: 0) + (it.asyncConfirmed.invoke()?.size ?: 0) + (it.asyncConfirmed.invoke()?.size ?: 0)
//                if (position == 0){
//                    handleSetBadgeBottomnav(R.id.menu_order, null)
//                }else{
//                    handleSetBadgeBottomnav(R.id.menu_order, 0)
//                }
//            }
//        }
    }

    private val broadcastReceiverNotify = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            productViewModel.handle(ProductAction.GetAllNotification)
        }
    }

    private val broadcastReceiverCall = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.extras?.getString("type")
            val idUrl = intent?.extras?.getString("idUrl")
            var intentCall = Intent(applicationContext, ChatActivity::class.java)
            startActivityWithData(intentCall, type, idUrl)
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiverNotify)
    }

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("CommitTransaction")
    private fun setupMainLayout() {
        supportFragmentManager.commit {
                add<MainFragment>(R.id.frame_layout).addToBackStack(MainFragment::class.java.simpleName)
        }
    }

    private fun handleEvent(event: PolyViewEvent) {
        when (event) {
            is HomeViewEvent -> {
                when (event) {
                    is HomeViewEvent.NavigateTo<*> -> addFragmentToBackStack(R.id.frame_layout,event.fragmentClass,event.fragmentClass.simpleName, bundle = event.bundle)
                   // is HomeViewEvent.ChangeDarkMode -> handleDarkMode(event.isCheckedDarkMode)
//                    is HomeViewEvent.SetBadgeBottomNav ->  handleSetBadgeBottomnav(event.id, event.position)
                    else -> {}
                }
            }
            is UserViewEvent -> {
                when (event) {
                    is UserViewEvent.ReturnFragment<*> -> addFragmentToBackStack(R.id.frame_layout,event.fragmentClass,event.fragmentClass.simpleName, bundle = event.bundle)
                    else -> {}
                }
            }
            is ProductEvent ->
                when(event) {
                    is ProductEvent.ReturnFragment<*> -> addFragmentToBackStack(R.id.frame_layout,event.fragmentClass,event.fragmentClass.simpleName, bundle = event.bundle)
                    else ->{

                    }
                }
        }

    }

    private fun handleSetBadgeBottomnav(idMenu: Int, position: Int?) {
//        if (position != null) {
//            if (position <= 0){
//                views.bottomNav.showBadge(idMenu)
//            }else{
//                views.bottomNav.showBadge(idMenu, position)
//            }
//        }
//        else{
//            views.bottomNav.dismissBadge(idMenu)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PayFragment.ACTIVITY_PAY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val resultDataBundle = data.getBundleExtra("resultDataPaymentIntent")
                if (resultDataBundle != null) {
                    val result: OrderResponse =
                        resultDataBundle.getSerializable("resultDataPaymentBundle") as OrderResponse
                    handlePaymentSuccess(result)
                }
            }
        }
    }

    private fun handlePaymentSuccess(result: OrderResponse) {
        productViewModel.handle(ProductAction.GetClearCart)
        productViewModel.handle(ProductAction.GetCurrentOrder(result._id))
        showUtilDialogWithCallback(
            Notify(
                "Đặt hàng thành công",
                "Chờ xác nhận",
                "Đơn hàng đã được gửi tới nhà hàng vui lòng chờ trong giây lát",
                R.raw.animation_successfully
            )
        ) {
            homeViewModel.returnOrderDetailFragment()
        }
    }

    private fun handleReceiveDataNotify() {
        val type = intent.extras?.getString("type")
        when (type) {
            MyConfigNotifi.TYPE_ORDER -> {
                homeViewModel.returnNotificationFragment()
            }
        }
    }

//    override fun onBackPressed() {
//        popBackStackAndShowPrevious()
////        if (views.bottomNav.getSelectedItemId() != R.id.menu_home && views.bottomNav.isVisible == true) {
////            views.bottomNav.setItemSelected(R.id.menu_home)
////        } else if (views.bottomNav.getSelectedItemId() == R.id.menu_home && views.bottomNav.isVisible == true) {
////            if (doubleClickBack) {
////                finishAffinity()
////            }
////            this.doubleClickBack = true
////            Toast.makeText(this, "Ấn Back lần nữa để thoát", Toast.LENGTH_SHORT).show()
////            Handler().postDelayed({ doubleClickBack = false }, 2000)
////        } else {
//            super.onBackPressed()
////        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        popBackStackAndShowPrevious()
    }


    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun create(initialState: ProductState): ProductViewModel {
        return productViewModelFactory.create(initialState)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityFactory.create(initialState)
    }

    override fun create(initialState: UserViewState): UserViewModel {
        return userViewModelFactory.create(initialState)
    }


}
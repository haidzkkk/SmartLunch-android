package com.fpoly.smartlunch.ui.main.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Notification
import com.fpoly.smartlunch.databinding.FragmentNotificationBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.notification.adapter.NotificationAdapter
import com.fpoly.smartlunch.ui.main.order.OrderAdapter
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.MyConfigNotifi

class NotificationFragment : PolyBaseFragment<FragmentNotificationBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setupAppBar()
        listenEvent()
    }

    private fun initData() {
        productViewModel.handle(ProductAction.GetAllNotification)
    }

    private fun setupAppBar() {
        views.appBar.apply {
            btnBackToolbar.visibility=View.VISIBLE
            tvTitleToolbar.text=getString(R.string.notification)
        }
    }
    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun handleOnNotificationClick(notification: Notification) {
        when(notification.type){
            MyConfigNotifi.TYPE_ORDER ->{
                productViewModel.handle(ProductAction.GetReadNotification(notification._id))
                productViewModel.handle(ProductAction.GetCurrentOrder(notification.idUrl))
                homeViewModel.returnOrderDetailFragment()
            }
            MyConfigNotifi.TYPE_COUPONS ->{

            }
        }

    }


    
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater,container,false)
    }

    override fun invalidate():Unit = withState(productViewModel) {
        when(it.asyncNotifications){
            is Success ->{
                val adapter = NotificationAdapter{notification->
                    handleOnNotificationClick(notification)
                }
                views.rcyNotification.adapter = adapter
                adapter.setData(it.asyncNotifications.invoke())
            }

            else -> {}
        }
        when(it.asyncReadNotification){
            is Success ->{
                initData()
                it.asyncReadNotification=Uninitialized
            }

            else -> {}
        }
    }

}
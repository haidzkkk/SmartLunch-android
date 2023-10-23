package com.fpoly.smartlunch.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.network.SocketManager
import com.fpoly.smartlunch.databinding.ActivityMainBinding
import com.fpoly.smartlunch.databinding.DialogHomeBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.HomeViewState
import com.fpoly.smartlunch.ui.main.home.TestViewModel
import com.fpoly.smartlunch.ui.main.home.TestViewModelMvRx
import javax.inject.Inject


class MainActivity : PolyBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val homeViewModel: HomeViewModel by viewModel()
    private val testViewModel : TestViewModel by lazy{ viewModelProvider.get(TestViewModel::class.java) }
    private val testViewModelMvRx: TestViewModelMvRx by viewModel()

    var doubleClickBack: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyConponent.inject(this)
        super.onCreate(savedInstanceState)
        setupStatusBar()
        setupViewpager()
        listenClickUI()

        Log.e("TAG", "activity: ${homeViewModel.hashCode()}")
        Log.e("TAG", "tesst viewmodel: ${testViewModel.hashCode()}")
        Log.e("TAG", "tesst viewmodel mvrx: ${testViewModelMvRx.hashCode()}")

        views.btnOk.setOnClickListener{
            startActivity(Intent(this, ChatActivity::class.java))

            PolyDialog.Builder(this, DialogHomeBinding.inflate(layoutInflater)).build().show()
        }
    }

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setupStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun setupViewpager() {


        views.viewpager.apply {
            this.adapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
            this.isUserInputEnabled = false
            this.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
            this.currentItem = 0
        }

        views.bottomNav.apply {
            this.setItemSelected(R.id.menu_home)
            this.setOnItemSelectedListener {
                when(it){
                    R.id.menu_home -> views.viewpager.currentItem = 0
                    R.id.menu_love -> views.viewpager.currentItem = 1
                    R.id.menu_card -> views.viewpager.currentItem = 2
                    R.id.menu_cart -> views.viewpager.currentItem = 3
                    R.id.menu_profile -> views.viewpager.currentItem = 4
                }
            }

            this.showBadge(R.id.menu_home)
            this.showBadge(R.id.menu_love)
            this.showBadge(R.id.menu_card)
            this.showBadge(R.id.menu_cart, 10)
            this.showBadge(R.id.menu_profile)
        }
    }

    private fun listenClickUI() {
    }



    override fun onBackPressed() {
        if(views.viewpager.currentItem != 0){
            views.viewpager.currentItem = 0
            views.bottomNav.setItemSelected(R.id.menu_home)
        }else{
            if (doubleClickBack) {
                super.onBackPressed()
            }

            this.doubleClickBack = true
            Toast.makeText(this, "Ấn Back lần nữa để thoát", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleClickBack = false }, 2000)
        }
    }


    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }



}
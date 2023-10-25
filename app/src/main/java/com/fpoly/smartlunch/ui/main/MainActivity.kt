package com.fpoly.smartlunch.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.core.PolyDialog
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivityMainBinding
import com.fpoly.smartlunch.databinding.DialogHomeBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.card.CardFragment
import com.fpoly.smartlunch.ui.main.cart.OrderFragment
import com.fpoly.smartlunch.ui.main.home.HomeFragment
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.HomeViewState
import com.fpoly.smartlunch.ui.main.home.TestViewModel
import com.fpoly.smartlunch.ui.main.home.TestViewModelMvRx
import com.fpoly.smartlunch.ui.main.love.FavouriteFragment
import com.fpoly.smartlunch.ui.main.product.ProductState
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.ProfileFragment
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewState
import com.fpoly.smartlunch.ultis.addFragmentToBackstack
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import javax.inject.Inject


class MainActivity : PolyBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory, ProductViewModel.Factory, UserViewModel.Factory {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    @Inject
    lateinit var productViewModelFactory: ProductViewModel.Factory

    @Inject
    lateinit var userViewModelFactory: UserViewModel.Factory

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sessionManager: SessionManager

    private val homeViewModel: HomeViewModel by viewModel()
    private val testViewModel : TestViewModel by lazy{ viewModelProvider.get(TestViewModel::class.java) }
    private val testViewModelMvRx: TestViewModelMvRx by viewModel()

    var doubleClickBack: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyConponent.inject(this)
        super.onCreate(savedInstanceState)
        setupBottomNavigation()
        handleViewModel()
        changeMode(sessionManager.fetchDarkMode())
        changeLanguage(sessionManager.fetchLanguage())
    }

    private fun handleViewModel() {
        homeViewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
    }

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setupBottomNavigation() {
        supportFragmentManager.commit { add<HomeFragment>(R.id.frame_layout) }
        views.bottomNav.apply {
            this.setItemSelected(R.id.menu_home)
            this.setOnItemSelectedListener { itemId ->
                val fragmentManager = supportFragmentManager
                val transaction = fragmentManager.beginTransaction()

                val fragment: Fragment? = when (itemId) {
                    R.id.menu_home -> fragmentManager.findFragmentByTag(HomeFragment.TAG)
                    R.id.menu_favourite -> fragmentManager.findFragmentByTag(FavouriteFragment.TAG)
                    R.id.menu_card -> fragmentManager.findFragmentByTag(CardFragment.TAG)
                    R.id.menu_order -> fragmentManager.findFragmentByTag(OrderFragment.TAG)
                    R.id.menu_profile -> fragmentManager.findFragmentByTag(ProfileFragment.TAG)
                    // Thêm các Fragment khác ở đây
                    else -> null
                }

                if (fragment == null) {
                    when (itemId) {
                        R.id.menu_home -> transaction.add(R.id.frame_layout, HomeFragment(), HomeFragment.TAG).addToBackStack(HomeFragment.TAG)
                        R.id.menu_favourite -> transaction.add(R.id.frame_layout, FavouriteFragment(), FavouriteFragment.TAG).addToBackStack(FavouriteFragment.TAG)
                        R.id.menu_favourite -> transaction.add(R.id.frame_layout, FavouriteFragment(), FavouriteFragment.TAG).addToBackStack(FavouriteFragment.TAG)
                        R.id.menu_card -> transaction.add(R.id.frame_layout, CardFragment(), CardFragment.TAG).addToBackStack(CardFragment.TAG)
                        R.id.menu_order -> transaction.add(R.id.frame_layout, OrderFragment(), OrderFragment.TAG).addToBackStack(OrderFragment.TAG)
                        R.id.menu_profile -> transaction.add(R.id.frame_layout, ProfileFragment(), ProfileFragment.TAG).addToBackStack(ProfileFragment.TAG)
                        // Thêm các Fragment khác ở đây
                    }
                } else {
                    transaction.show(fragment)
                }

                val fragments = fragmentManager.fragments
                for (f in fragments) {
                    if (f != fragment) {
                        transaction.hide(f)
                    }
                }
                transaction.commit()
            }
            visibilityBottomNav(true)
        }
    }

    private fun handleEvent(event: HomeViewEvent) {
        when (event) {
            is HomeViewEvent.ReturnFragment<*> -> { addFragmentToBackstack(R.id.frame_layout, event.fragmentClass) }
            is HomeViewEvent.ReturnVisibleBottomNav -> visibilityBottomNav(event.isVisibleBottomNav)
            is HomeViewEvent.ChangeDarkMode -> handleDarkMode(event.isCheckedDarkMode)
        }

    }

    private fun handleDarkMode(checkedDarkMode: Boolean) {
        sessionManager.saveDarkMode(checkedDarkMode)
        changeMode(checkedDarkMode)
        changeLanguage(sessionManager.fetchLanguage())
    }

    fun visibilityBottomNav(isVisible: Boolean){
        views.bottomNav.isVisible = isVisible
    }

    override fun onBackPressed() {

        if(views.bottomNav.getSelectedItemId() != R.id.menu_home && views.bottomNav.isVisible == true){
            views.bottomNav.setItemSelected(R.id.menu_home)
        }else if(views.bottomNav.getSelectedItemId() == R.id.menu_home && views.bottomNav.isVisible == true){
            if (doubleClickBack) { finishAffinity() }
            this.doubleClickBack = true
            Toast.makeText(this, "Ấn Back lần nữa để thoát", Toast .LENGTH_SHORT).show()
            Handler().postDelayed({ doubleClickBack = false }, 2000)
        } else{
            super.onBackPressed()
        }
    }


    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun create(initialState: ProductState): ProductViewModel {
        return productViewModelFactory.create(initialState)
    }

    override fun create(initialState: UserViewState): UserViewModel {
        return userViewModelFactory.create(initialState)
    }


}
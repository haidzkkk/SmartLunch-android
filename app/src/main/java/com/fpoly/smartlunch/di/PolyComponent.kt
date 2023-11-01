package com.fpoly.smartlunch.di

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.di.modules.FragmentModule
import com.fpoly.smartlunch.di.modules.NetworkModule
import com.fpoly.smartlunch.di.modules.ViewModelModule
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.chat.home.HomeChatFragment
import com.fpoly.smartlunch.ui.main.MainActivity
import com.fpoly.smartlunch.ui.main.payment.AddressPaymentFragment
import com.fpoly.smartlunch.ui.main.payment.PayFragment
import com.fpoly.smartlunch.ui.main.payment.PaymentActivity
import com.fpoly.smartlunch.ui.main.profile.AddressFragment
import com.fpoly.smartlunch.ui.main.profile.ChangePasswordFragment
import com.fpoly.smartlunch.ui.main.profile.LanguageFragment
import com.fpoly.smartlunch.ui.main.profile.ProfileFragment
import com.fpoly.smartlunch.ui.security.LoginActivity
import com.fpoly.smartlunch.ui.security.LoginFragment
import com.fpoly.smartlunch.ui.security.SplashScreenActivity
import com.fpoly.smartlunch.ui.security.onboarding.ThirdFragment
import dagger.BindsInstance
import dagger.Component

@Component(modules = [
    ViewModelModule::class,
    NetworkModule::class,
    FragmentModule::class
])
interface PolyComponent {

    fun inject(application: PolyApplication)
    fun inject(activity: SplashScreenActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: ChatActivity)
    fun inject(activity: PaymentActivity)
    fun inject(fragment: ThirdFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: HomeChatFragment)

    fun inject(fragment: PayFragment)
    fun inject(fragment: AddressFragment)
    fun inject(fragment: AddressPaymentFragment)
    fun inject(fragment: LanguageFragment)
    fun inject(fragment: ChangePasswordFragment)
    fun fragmentFactory(): FragmentFactory
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): PolyComponent
    }
}
package com.fpoly.smartlunch.di

import android.content.Context
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.di.modules.FragmentModule
import com.fpoly.smartlunch.di.modules.NetworkModule
import com.fpoly.smartlunch.di.modules.ViewModelModule
import com.fpoly.smartlunch.ui.call.CallActivity
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.chat.home.HomeChatFragment
import com.fpoly.smartlunch.ui.main.MainActivity
import com.fpoly.smartlunch.ui.main.MainFragment
import com.fpoly.smartlunch.ui.payment.address.AddressPaymentFragment
import com.fpoly.smartlunch.ui.payment.payment.PayFragment
import com.fpoly.smartlunch.ui.payment.PaymentActivity
import com.fpoly.smartlunch.ui.main.profile.ChangePasswordFragment
import com.fpoly.smartlunch.ui.main.profile.LanguageFragment
import com.fpoly.smartlunch.ui.main.profile.ProfileFragment
import com.fpoly.smartlunch.ui.paynow.PayNowActivity
import com.fpoly.smartlunch.ui.security.LoginActivity
import com.fpoly.smartlunch.ui.security.LoginFragment
import com.fpoly.smartlunch.ui.security.SplashScreenActivity
import com.fpoly.smartlunch.ui.security.onboarding.FirstFragment
import com.fpoly.smartlunch.ui.security.onboarding.SecondFragment
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
    fun inject(activity: CallActivity)
    fun inject(activity: PaymentActivity)
    fun inject(activity: PayNowActivity)
    fun inject(fragment: MainFragment)
    fun inject(fragment: FirstFragment)
    fun inject(fragment: SecondFragment)
    fun inject(fragment: ThirdFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: HomeChatFragment)

    fun inject(fragment: PayFragment)
    fun inject(fragment: com.fpoly.smartlunch.ui.paynow.payment.PaymentNowFragment)
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
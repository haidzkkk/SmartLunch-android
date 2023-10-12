package com.fpoly.smartlunch.core

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.fpoly.smartlunch.di.DaggerPolyConponent
import com.fpoly.smartlunch.di.HasScreenInjector
import com.fpoly.smartlunch.di.PolyConponent
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class PolyBaseActivity<VB: ViewBinding> : AppCompatActivity(), HasScreenInjector {

    protected lateinit var views: VB

    private lateinit var fragmentFactory: FragmentFactory
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    private var savedInstanceState: Bundle? = null
    private lateinit var appComponent: PolyConponent


    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent = DaggerPolyConponent.factory().create(this)
        fragmentFactory = appComponent.fragmentFactory()
        viewModelFactory = appComponent.viewModelFactory()
        supportFragmentManager.fragmentFactory = fragmentFactory  //giúp quản lý vòng đời của Fragment trong khi khôi phục trạng thái của activity và đảm bảo rằng việc tái tạo Fragment diễn ra đúng cách  ||||  Việc xác định FragmentFactory trong supportFragmentManager giúp đảm bảo rằng FragmentManager sẽ sử dụng FragmentFactory đã cung cấp để tái tạo các Fragment, đảm bảo rằng các tham số của Fragment như arguments hay dữ liệu trạng thái được giữ nguyên đúng cách.

        super.onCreate(savedInstanceState)
        views = getBinding()
        setContentView(views.root)

        this.savedInstanceState = savedInstanceState
    }

    abstract fun getBinding(): VB

    /**
     * use when listen callback viewevents from viewModel
     */
    @SuppressLint("CheckResult")
    protected fun <T : PolyViewEvent> PolyBaseViewModel<*, *, T>.observeViewEvents(observer: (T?) -> Unit) {
        viewEvents
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                observer(it)
            }
    }


    /**
     * use when create instance of fragment
     */
    protected fun createFragment(fragmentClass: Class<out Fragment>, args: Bundle?): Fragment {
        return fragmentFactory.instantiate(classLoader, fragmentClass.name).apply {
            arguments = args
        }
    }

    /**
     * use when create viewModel
     */
    protected val viewModelProvider
        get() = ViewModelProvider(this, viewModelFactory)


    override fun injector(): PolyConponent {
        return appComponent
    }
}
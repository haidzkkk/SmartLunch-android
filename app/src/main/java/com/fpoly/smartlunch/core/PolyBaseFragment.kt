package com.fpoly.smartlunch.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.BaseMvRxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class PolyBaseFragment<VB: ViewBinding> : BaseMvRxFragment() {

    private var _binding: VB? = null
    protected val views: VB get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = getBinding(inflater, container)
        return views.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        uiDisposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
//        uiDisposables.dispose()
    }

/* ==========================================================================================
 * Disposable : để quản lý viewEvents thôi
 * ========================================================================================== */
    private val uiDisposables = CompositeDisposable()

    protected fun Disposable.disposeOnDestroyView() {
        uiDisposables.add(this)
    }

    protected fun <T : PolyViewEvent> PolyBaseViewModel<*, *, T>.observeViewEvents(observer: (T) -> Unit) {
        viewEvents
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                observer(it)
            }
            .disposeOnDestroyView()
    }



    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): VB
}
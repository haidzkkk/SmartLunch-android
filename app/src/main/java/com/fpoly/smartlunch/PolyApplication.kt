package com.fpoly.smartlunch

import android.app.Application
import com.fpoly.smartlunch.di.DaggerPolyConponent
import com.fpoly.smartlunch.di.PolyConponent

class PolyApplication : Application() {

    val polyConponent: PolyConponent by lazy{
        DaggerPolyConponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        polyConponent.inject(this)
    }

}
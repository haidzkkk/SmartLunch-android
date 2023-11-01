package com.fpoly.smartlunch

import android.app.Application
import com.fpoly.smartlunch.di.DaggerPolyComponent
import com.fpoly.smartlunch.di.PolyComponent

class PolyApplication : Application() {

    val polyComponent: PolyComponent by lazy {
        DaggerPolyComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        polyComponent.inject(this)
    }

}
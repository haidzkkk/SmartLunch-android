package com.fpoly.smartlunch.ui.main.home

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import kotlin.random.Random

class TestViewModel @Inject constructor() : ViewModel() {

    fun test(): String = "this is test viewModel: ${Random.nextInt()}"
}
package com.fpoly.smartlunch.ui.security

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : PolyBaseFragment<FragmentForgotPasswordBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()
    private var isEmailFilled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        views.btnSend.isEnabled = false
        views.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validateEmailInput()
            }
        })
        views.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        views.tvSignUp.setOnClickListener {
            viewModel.handleReturnSignUpEvent()
        }
        views.btnSend.setOnClickListener {
            val email = views.edtEmail.text.toString()
            viewModel.handle(SecurityViewAction.ForgotPassword(email))
        }
    }

    private fun validateEmailInput() {
        val email = views.edtEmail.text.toString().trim()
        isEmailFilled = email.isNotEmpty()
        views.btnSend.isEnabled = isEmailFilled
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncForgotPassword) {
            is Success -> {
                viewModel.handleReturnVerifyOTPEvent()
            }

            is Fail -> {
            }

            else -> {}
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotPasswordBinding {
        return FragmentForgotPasswordBinding.inflate(inflater, container, false);
    }

}
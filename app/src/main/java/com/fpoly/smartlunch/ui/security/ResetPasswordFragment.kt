package com.fpoly.smartlunch.ui.security

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.model.ResetPasswordRequest
import com.fpoly.smartlunch.data.model.VerifyOTPRequest
import com.fpoly.smartlunch.databinding.FragmentChangePasswordBinding
import com.fpoly.smartlunch.databinding.FragmentResetPasswordBinding
import com.fpoly.smartlunch.ultis.showUtilDialog

class ResetPasswordFragment : PolyBaseFragment<FragmentResetPasswordBinding>(),TextWatcher {
    private val viewModel: SecurityViewModel by activityViewModel()
    private var isFieldsFilled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        views.btnSend.isEnabled = false
        views.newPassword.addTextChangedListener(this)
        views.confirmPassword.addTextChangedListener(this)
        views.btnSend.setOnClickListener {
            val newPassword= views.newPassword.text.toString()
            val confirmPassword= views.confirmPassword.text.toString()
            val userId = withState(viewModel){
                it.asyncForgotPassword.invoke()?.data?.userId
            }
            if (userId!=null){
                viewModel.handle(SecurityViewAction.ResetPassword(
                    ResetPasswordRequest(userId,newPassword,confirmPassword))
                )
            }
        }
    }

    private fun validateFields() {
        val newPassword = views.newPassword.text.toString()
        val confirmPassword = views.confirmPassword.text.toString()
        var isValid = true

        if (newPassword.isEmpty()) {
            views.newPasswordTil.error = getString(R.string.password_not_empty)
            isValid = false
        } else {
            views.newPasswordTil.error = null
        }

        if (confirmPassword.isEmpty()) {
            views.confirmPasswordTil.error = getString(R.string.confirm_password_not_empty)
            isValid = false
        } else {
            views.confirmPasswordTil.error = null
        }

        isFieldsFilled = isValid
        views.btnSend.isEnabled = isFieldsFilled
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when(it.asyncUserCurrent){
            is Success -> {
                activity?.showUtilDialog(
                    Notify(
                        getString(R.string.reset_password_success_title),
                        "${it.asyncUserCurrent.invoke()?.email}",
                        getString(R.string.reset_password_success_message),
                        R.raw.animation_successfully
                    )
                )
                viewModel.handleReturnLogin()
                viewModel.handleRemoveAsyncSignUp()
            }
            is Fail ->{
            }

            else -> {}
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentResetPasswordBinding {
        return FragmentResetPasswordBinding.inflate(inflater, container, false);
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        validateFields()
    }
}
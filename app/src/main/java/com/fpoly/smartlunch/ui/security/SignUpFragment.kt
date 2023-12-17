package com.fpoly.smartlunch.ui.security

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.UserRequest
import com.fpoly.smartlunch.data.model.VerifyOTPResponse
import com.fpoly.smartlunch.databinding.FragmentSignUpBinding
import com.fpoly.smartlunch.ultis.checkPhoneNumberValid
import com.fpoly.smartlunch.ultis.checkValidEPassword
import com.fpoly.smartlunch.ultis.checkValidEmail
import com.fpoly.smartlunch.ultis.showSnackbar

class SignUpFragment : PolyBaseFragment<FragmentSignUpBinding>(), TextWatcher {

    private val viewModel: SecurityViewModel by activityViewModel()

    private var isSendButtonEnabled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        views.send.isEnabled = false
        views.confirmPassword.setOnEditorActionListener{v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                views.send.performClick()
            }
            false
        }

        views.send.setOnClickListener {
            signupSubmit()
        }
        views.tvSignIn.setOnClickListener {
            viewModel.handleReturnLogin()
        }
        views.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        views.firstName.addTextChangedListener(this)
        views.lastName.addTextChangedListener(this)
        views.email.addTextChangedListener(this)
        views.phone.addTextChangedListener(this)
        views.password.addTextChangedListener(this)
        views.confirmPassword.addTextChangedListener(this)
    }

    private fun signupSubmit() {
        val user = UserRequest(
            first_name = views.firstName.text.toString(),
            last_name = views.lastName.text.toString(),
            email = views.email.text.toString(),
            phone = views.phone.text.toString(),
            password = views.password.text.toString(),
            confirmPassword = views.confirmPassword.text.toString()
        )
        viewModel.handle(SecurityViewAction.SignupAction(user))
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncSignUp) {
            is Success -> {
                viewModel.handleReturnVerifyOTPEvent()
            }

            is Fail -> {
                val error= (it.asyncSignUp as Fail<VerifyOTPResponse>).error.message.toString().trim()
                when(error){
                    "HTTP 500 Internal Server Error" ->{
                        showSnackbar(requireView(),getString(R.string.already_registered),false,getString(R.string.login),{
                            activity?.onBackPressed()
                        })
                    }
                    else ->{
                        showSnackbar(requireView(),getString(R.string.system_error),false,getString(R.string.ok),{})
                    }
                }
            }

            else -> {}
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(inflater, container, false)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val views = views
        val isFieldsFilled = views.run {
            firstName.text?.isNotEmpty() == true &&
                    lastName.text?.isNotEmpty() == true &&
                    email.text?.isNotEmpty() == true &&
                    phone.text?.isNotEmpty() == true &&
                    password.text?.isNotEmpty() == true &&
                    confirmPassword.text?.isNotEmpty() == true &&
                    emailTil.checkValidEmail(context?.resources) == false &&
                    phoneTil.editText?.checkPhoneNumberValid(context?.resources) == false &&
                    context?.resources?.let { confirmPasswordTil.checkValidEPassword(it,password,confirmPassword) } == false
        }

        isSendButtonEnabled = isFieldsFilled
        views.send.isEnabled = isSendButtonEnabled

        if (s != null && s.isNotEmpty()) {
            when (s) {
                views.firstName.text -> views.firstNameTil.error = null
                views.lastName.text -> views.lastNameTil.error = null
                views.email.text -> views.emailTil.error = null
                views.phone.text -> views.phoneTil.error = null
                views.password.text -> views.passwordTil.error = null
                views.confirmPassword.text -> views.confirmPasswordTil.error = null
            }
        }
    }
}

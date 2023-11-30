package com.fpoly.smartlunch.ui.main.profile

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
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentChangePasswordBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.profile.adapter.LanguageAdapter
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.showUtilDialog
import javax.inject.Inject

class ChangePasswordFragment : PolyBaseFragment<FragmentChangePasswordBinding>(), TextWatcher {
    private val viewModel: UserViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    @Inject
    lateinit var sessionManager: SessionManager

    private var isSendButtonEnabled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        listenEvent()
    }

    private fun setupUI() {
        views.appBar.apply {
            btnBackToolbar.visibility = View.VISIBLE
            tvTitleToolbar.text = ""
        }
    }

    private fun listenEvent() {
        views.btnSend.isEnabled = false
        views.currentPassword.addTextChangedListener(this)
        views.newPassword.addTextChangedListener(this)
        views.confirmPassword.addTextChangedListener(this)
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.btnSend.setOnClickListener {
            submitForm()
        }
    }

    private fun submitForm() {
        val currentPassword = views.currentPassword.text.toString()
        val newPassword = views.newPassword.text.toString()
        val confirmPassword = views.confirmPassword.text.toString()
        if (checkPasswordMatches(newPassword, confirmPassword)) {
            viewModel.handle(
                UserViewAction.ChangePasswordUser(
                    ChangePassword(
                        currentPassword,
                        newPassword
                    )
                )
            )
        } else {
            setErrorsIfPasswordNotMatches()
        }
    }

    private fun setErrorsIfPasswordNotMatches() {
        views.confirmPasswordTil.error = getString(R.string.password_not_match)
    }

    private fun checkPasswordMatches(newPassword: String, confirmPassword: String): Boolean {
        return newPassword == confirmPassword
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncChangePassword) {
            is Success -> {
                it.asyncChangePassword.invoke()?.let { token ->
                    token.accessToken?.let { accessToken ->
                        sessionManager.saveAuthTokenAccess(
                            accessToken
                        )
                    }
                    token.refreshToken?.let { refreshToken ->
                        sessionManager.saveAuthTokenRefresh(
                            refreshToken
                        )
                    }
                }
                activity?.showUtilDialog(
                    Notify(
                        getString(R.string.reset_password_success_title),
                        "${it.asyncCurrentUser.invoke()?.email}",
                        getString(R.string.change_password_success_message),
                        R.raw.animation_successfully
                    )
                )
                activity?.onBackPressed()
                viewModel.handleRemoveAsyncChangePassword()
            }

            is Fail -> {

            }

            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.returnVisibleBottomNav(true)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangePasswordBinding {
        return FragmentChangePasswordBinding.inflate(inflater, container, false);
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val views = views
        val isFieldsFilled = views.run {
            currentPassword.text?.isNotEmpty() == true &&
                    newPassword.text?.isNotEmpty() == true &&
                    confirmPassword.text?.isNotEmpty() == true
        }

        isSendButtonEnabled = isFieldsFilled
        views.btnSend.isEnabled = isSendButtonEnabled
    }

}
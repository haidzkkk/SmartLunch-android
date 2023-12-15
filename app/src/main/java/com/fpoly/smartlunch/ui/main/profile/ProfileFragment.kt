package com.fpoly.smartlunch.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.DialogHomeBinding
import com.fpoly.smartlunch.databinding.FragmentProfileBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.security.SecurityViewModel
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import com.fpoly.smartlunch.ultis.handleLogOut
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class ProfileFragment : PolyBaseFragment<FragmentProfileBinding>() {
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    companion object{
        const val TAG = "ProfileFragment"
    }
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        configData()
        setupUI()
        listenEvent()
    }

    private fun setupUI(): Unit = withState(userViewModel){
        val user = it.asyncCurrentUser.invoke()
        views.apply {
            if (user != null) {
                Glide.with(requireContext())
                    .load(user.avatar?.url)
                    .placeholder(R.drawable.baseline_person_outline_24)
                    .error(R.drawable.baseline_person_outline_24)
                    .into(imgAvatar)
                displayName.text = "${user.first_name} ${user.last_name}"
            }
        }
    }

    private fun configData() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun listenEvent() {
        views.btnEditProfile.setOnClickListener {
            homeViewModel.returnEditProfileFragment()
        }
        views.layoutLocation.setOnClickListener {
            homeViewModel.returnAddressFragment()
        }
        views.layoutChangePass.setOnClickListener {
        homeViewModel.returnChangePasswordFragment()
        }
        views.layoutLanguage.setOnClickListener{
            homeViewModel.returnLanguageFragment()
        }

        views.layoutNotifies.setOnClickListener {
            homeViewModel.returnNotificationFragment()
        }

        views.layoutChat.setOnClickListener{
            activity?.startActivity(Intent(requireContext(), ChatActivity::class.java))
        }

        views.logout.setOnClickListener {
            userViewModel.handle(UserViewAction.LogOutUser)
        }
    }

    private fun handleDarkMode(checkedDarkMode: Boolean) {
        sessionManager.saveDarkMode(checkedDarkMode)
        changeMode(checkedDarkMode)
        activity?.changeLanguage(sessionManager.fetchLanguage())
        activity?.recreate()
    }

    private fun logOutGG() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                Log.w("Logout", "Sign out success")
            }
            .addOnFailureListener { e ->
                Log.w("Logout", "Sign out failed", e)
            }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)

    override fun invalidate() {
        withState(userViewModel){
            when(it.asyncLogout){
                is Success ->{
                    activity?.handleLogOut()
                    logOutGG()
                }
                is Fail ->{
                    activity?.handleLogOut()
                    logOutGG()
                }
                else -> { }
            }
        }
    }

}
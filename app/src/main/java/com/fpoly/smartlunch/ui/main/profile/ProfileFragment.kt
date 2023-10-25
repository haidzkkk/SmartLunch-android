package com.fpoly.smartlunch.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import javax.inject.Inject

class ProfileFragment : PolyBaseFragment<FragmentProfileBinding>() {
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()
    companion object{
        const val TAG = "ProfileFragment"
    }

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyConponent.inject(this)
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

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(true)
    }

    private fun configData() {
        sessionManager.fetchDarkMode().let { views.switchDarkMode.isChecked = it }
    }

    private fun listenEvent() {
        views.btnEditProfile.setOnClickListener {
            homeViewModel.returnEditProfileFragment()
        }
        views.layoutLocation.setOnClickListener {  }
        views.layoutChangePass.setOnClickListener {
        homeViewModel.returnChangePasswordFragment()
        }
        views.layoutLanguage.setOnClickListener{
//            showOptionMenu{strLang ->
//                sessionManager.let {
//                    activity?.changeLanguage(strLang)
//                    it.saveLanguage(strLang)
//                }
//            }
            homeViewModel.returnLanguageFragment()
        }
        sessionManager.fetchDarkMode().let {
            views.switchDarkMode.isChecked=it
        }
        views.switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            homeViewModel.handleChangeThemeMode(isChecked)
        }
//        views.switchDarkMode.apply {
//            this.setTrackResource(R.drawable.track_switch)
//            this.setThumbResource(R.drawable.thumb_switch)
//            this.setOnCheckedChangeListener { _, isChecked ->
//                changeMode(isChecked)
//                sessionManager.let { it.saveDarkMode(isChecked) }
//            }
//        }

        views.layoutChat.setOnClickListener{
            activity?.startActivity(Intent(requireContext(), ChatActivity::class.java))
        }

        views.logout.setOnClickListener {
            activity?.handleLogOut()
        }
    }

    private fun showOptionMenu(callBack: (id: String) -> Unit) {
        val popUpMenu = PopupMenu(requireContext(), views.layoutLanguage)
        popUpMenu.inflate(R.menu.menu_lang)
        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_en -> {
                    callBack("en")
                }
                R.id.menu_vi -> {
                    callBack("vi")
                }
            }
            true
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)

    override fun invalidate() {

    }
}
package com.fpoly.smartlunch.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentProfileBinding
import com.fpoly.smartlunch.ui.security.SecurityViewModel
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import com.fpoly.smartlunch.ultis.handleLogOut
import javax.inject.Inject

class ProfileFragment : PolyBaseFragment<FragmentProfileBinding>() {
    //private val securityViewModel: SecurityViewModel by activityViewModel()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyConponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        configData()
        listenClickUI()
    }

    private fun configData() {
        sessionManager.fetchDarkMode().let { views.switchDarkMode.isChecked = it }

    }

    private fun listenClickUI() {
        views.layoutLocation.setOnClickListener {  }
        views.layoutChangePass.setOnClickListener {

        }
        views.layoutLanguage.setOnClickListener{
            showOptionMenu{strLang ->
                sessionManager.let {
                    activity?.changeLanguage(strLang)
                    it.saveLanguage(strLang)
                }
            }
        }
        views.switchDarkMode.apply {
            this.setTrackResource(R.drawable.track_switch)
            this.setThumbResource(R.drawable.thumb_switch)
            this.setOnCheckedChangeListener { _, isChecked ->
                changeMode(isChecked)
                sessionManager.let { it.saveDarkMode(isChecked) }
            }
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
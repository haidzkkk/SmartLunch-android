package com.fpoly.smartlunch.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentProfileBinding
import com.fpoly.smartlunch.ultis.changeLangue
import com.fpoly.smartlunch.ultis.changeMode
import javax.inject.Inject

class ProfileFragment : PolyBaseFragment<FragmentProfileBinding>() {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)

    override fun invalidate() {
    }

    @Inject
    public lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyConponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        configData()
        listenClickUI()
    }

    private fun configData() {
        sessionManager.fetchDarkMode().let { views.switchMode.isChecked = it ?: false }

        views.tvLanguage.text = sessionManager.fetchLanguage().let {
            if (it == "vi") getString(R.string.vi)
            else getString(R.string.en)
        }
    }

    private fun listenClickUI() {
        views.switchMode.apply {
            this.setTrackResource(R.drawable.track_switch)
            this.setThumbResource(R.drawable.thumb_switch)
            this.setOnCheckedChangeListener { _, isChecked ->
                changeMode(isChecked)
                sessionManager.let { it.saveDarkMode(isChecked) }
            }
        }

        views.tvLanguage.setOnClickListener{
            showOptionMenu{strLang ->
                sessionManager.let {
                    changeLangue(resources, strLang)
                    it.saveLanguage(strLang)
                }
            }
        }
    }

    private fun showOptionMenu(callBack: (id: String) -> Unit) {
        val popUpMenu = PopupMenu(requireContext(), views.tvLanguage)
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

}
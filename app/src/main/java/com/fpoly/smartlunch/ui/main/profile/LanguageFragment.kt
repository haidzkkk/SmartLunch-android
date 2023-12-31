package com.fpoly.smartlunch.ui.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.activityViewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Language
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentLanguageBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.MainActivity
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.profile.adapter.LanguageAdapter
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.fpoly.smartlunch.ultis.changeLanguage
import javax.inject.Inject

class LanguageFragment : PolyBaseFragment<FragmentLanguageBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        listenEvent()
    }

    private fun setupUI() {
        views.appBar.tvTitleToolbar.text = getText(R.string.language)
        val adapter = LanguageAdapter{ language ->
            sessionManager.let {
                activity?.changeLanguage(language.code)
                it.saveLanguage(language.code)
            }
        }
        adapter.setData(createLanguageList())
        views.rcyLanguage.adapter = adapter
    }

    private fun listenEvent() {
        views.btnSaveLanguage.setOnClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString("type", MyConfigNotifi.TYPE_LANGUAGE_RESET)
                    }
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            startActivity(intent)
           activity?.finish()
        }
    }

    private fun createLanguageList(): List<Language> {
        val languages = ArrayList<Language>()
        languages.add(
            Language(
                getString(R.string.en),
                getString(R.string.en_code),
                sessionManager.fetchLanguage() == getString(R.string.en_code)
            )
        )
        languages.add(
            Language(
                getString(R.string.vi),
                getString(R.string.vi_code),
                sessionManager.fetchLanguage() == getString(R.string.vi_code)
            )
        )
        languages.add(
            Language(
                getString(R.string.zh),
                getString(R.string.zh_code),
                sessionManager.fetchLanguage() == getString(R.string.zh_code)
            )
        )
        languages.add(
            Language(
                getString(R.string.ko),
                getString(R.string.ko_code),
                sessionManager.fetchLanguage() == getString(R.string.ko_code)
            )
        )
        languages.add(
            Language(
                getString(R.string.th),
                getString(R.string.th_code),
                sessionManager.fetchLanguage() == getString(R.string.th_code)
            )
        )
    
        return languages
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLanguageBinding {
        return FragmentLanguageBinding.inflate(inflater, container, false)
    }

    override fun invalidate() {
    }

}

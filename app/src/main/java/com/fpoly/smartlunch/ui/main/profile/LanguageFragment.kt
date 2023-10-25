package com.fpoly.smartlunch.ui.main.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Language
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentLanguageBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.profile.adapter.LanguageAdapter
import com.fpoly.smartlunch.ultis.changeLanguage
import javax.inject.Inject

class LanguageFragment : PolyBaseFragment<FragmentLanguageBinding>() {
    private val homeViewModel: HomeViewModel by activityViewModel()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyConponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        listenEvent()
    }

    private fun setupUI() {
        val languages = createLanguageList() // Thay thế bằng danh sách ngôn ngữ thực tế
        val adapter = LanguageAdapter(languages) { language ->
            sessionManager.let {
                activity?.changeLanguage(language.code)
                it.saveLanguage(language.code)
            }
        }
        views.rcyLanguage.adapter = adapter
    }

    private fun listenEvent() {
        // Thêm xử lý sự kiện khi ngôn ngữ được chọn
    }

    private fun createLanguageList(): List<Language> {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("selected_language", "en") // Lấy ngôn ngữ được lưu trong local

        val languages = ArrayList<Language>()
        languages.add(Language("English", "en", selectedLanguage == "en"))
        languages.add(Language("Vietnamese", "vi", selectedLanguage == "vi"))
        return languages
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
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

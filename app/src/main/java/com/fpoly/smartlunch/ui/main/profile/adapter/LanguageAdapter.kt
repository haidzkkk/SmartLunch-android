package com.fpoly.smartlunch.ui.main.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.data.model.Language
import com.fpoly.smartlunch.databinding.ItemLanguageBinding

class LanguageAdapter(val onClick: (Language) -> Unit) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {
    private var languages: List<Language> = listOf()
    fun setData(list: List<Language>) {
        languages = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder =
        LanguageViewHolder(
            ItemLanguageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.bind(language)
    }

    override fun getItemCount() = languages.size

    inner class LanguageViewHolder(private val itemLanguageBinding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(itemLanguageBinding.root) {
        fun bind(language: Language) {
            itemLanguageBinding.tvLanguageName.text = language.name
            itemLanguageBinding.radioButtonLanguage.isChecked = language.isSelected
            itemLanguageBinding.radioButtonLanguage.setOnClickListener {
                onClick(language)
                for (item in languages) {
                    item.isSelected = (item == language)
                }
                notifyDataSetChanged()
            }
            itemLanguageBinding.root.setOnClickListener{
                onClick(language)
                for (item in languages) {
                    item.isSelected = (item == language)
                }
                notifyDataSetChanged()
            }
        }
    }
}


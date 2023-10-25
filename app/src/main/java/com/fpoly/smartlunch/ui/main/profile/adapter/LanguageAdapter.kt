package com.fpoly.smartlunch.ui.main.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.Language

class LanguageAdapter(private val languages: List<Language>, val onClick: (Language) -> Unit) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.bind(language)
    }

    override fun getItemCount() = languages.size

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val radioButton: RadioButton = itemView.findViewById(R.id.radio_button_language)
        private val textViewName: TextView = itemView.findViewById(R.id.tv_languageName)

        fun bind(language: Language) {
            textViewName.text = language.name
            radioButton.isChecked = language.isSelected

            radioButton.setOnClickListener {
                onClick(language)
                // Đánh dấu ngôn ngữ đã chọn
                for (item in languages) {
                    item.isSelected = (item == language)
                }
                notifyDataSetChanged()
            }
        }
    }
}


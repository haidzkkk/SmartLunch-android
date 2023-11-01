package com.fpoly.smartlunch.ultis

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.databinding.UtilDialogLayoutBinding
import java.util.Calendar

fun Activity.showDatePickerDialog(callback: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        this,
        R.style.CustomDatePickerDialogTheme,
        { view, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
            callback(selectedDate)
        },
        year,
        month,
        day
    )
    datePickerDialog.show()
}

fun Activity.showUtilDialog(
    notify: Notify
) {
    val dialog = PolyDialog.Builder(this, UtilDialogLayoutBinding.inflate(layoutInflater))
        .isBorderRadius(true)
        .isWidthMatchParent(false)
        .isHeightMatchParent(false)
        .isTransparent(true)
        .layoutGravity(PolyDialog.GRAVITY_CENTER)
        .build()

    dialog.setCancelable(true)
    dialog.show()

    dialog.viewsDialog.apply {
        tvHeading.text = notify.heading
        tvTitle.text = notify.title
        tvNotifyContent.text = notify.content
        animDialog.setAnimation(notify.animationId)
        animDialog.playAnimation()
    }

    dialog.viewsDialog.btnDismiss.setOnClickListener {
        dialog.dismiss()
    }
}

fun Activity.showUtilDialogWithCallback(
    notify: Notify,
    callback: (() -> Unit)? = null
) {
    val dialog = PolyDialog.Builder(this, UtilDialogLayoutBinding.inflate(layoutInflater))
        .isBorderRadius(true)
        .isWidthMatchParent(false)
        .isHeightMatchParent(false)
        .isTransparent(true)
        .layoutGravity(PolyDialog.GRAVITY_CENTER)
        .build()

    dialog.setCancelable(true)
    dialog.show()

    dialog.viewsDialog.apply {
        tvHeading.text = notify.heading
        tvTitle.text = notify.title
        tvNotifyContent.text = notify.content
        btnDismiss.apply {
            text = getString(R.string.home)
            setBackgroundColor(Color.GRAY)
        }
        btnContinute.visibility = View.VISIBLE
        animDialog.setAnimation(notify.animationId)
        animDialog.playAnimation()
    }

    dialog.viewsDialog.btnDismiss.setOnClickListener {
        dialog.dismiss()
    }
    dialog.viewsDialog.btnContinute.setOnClickListener {
        callback?.invoke()
        dialog.dismiss()
    }
}

package com.fpoly.smartlunch.ultis

import android.app.Activity
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.databinding.UtilDialogLayoutBinding


fun Activity.showUtilDialog(
    notify: Notify
) {
    val dialog = PolyDialog.Builder(this, UtilDialogLayoutBinding.inflate(layoutInflater))
        .isBorderRadius(true)
        .isWidthMatchParent(true)
        .isHeightMatchParent(false)
        .isTransparent(true)
        .layoutGravity(PolyDialog.GRAVITY_CENTER)
        .build()

    dialog.setCancelable(true)
    dialog.show()

    dialog.viewsDialog.apply {
        tvHeading.text=notify.heading
        tvTitle.text=notify.title
        tvNotifyContent.text= notify.content
        animDialog.setAnimation(notify.animationId)
        animDialog.playAnimation()
    }

    dialog.viewsDialog.btnDismiss.setOnClickListener {
        dialog.dismiss()
    }
}

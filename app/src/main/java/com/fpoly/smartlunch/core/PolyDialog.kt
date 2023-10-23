package com.fpoly.smartlunch.core

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.fpoly.smartlunch.R

class PolyDialog<VB: ViewBinding>(
    private val context: Context,                               // context
    val binding: VB,                                            // binding
    private val isBorderRadius: Boolean,                        // bo góc
    private val isTransparent: Boolean,                         // trong suốt
    private val isWidthMatchParent: Boolean,                    // chieu rộng
    private val isHeightMatchParent: Boolean,                   // chieeu cao, đừng để RelativeLayout nó sẽ kh wrapcontent dưuoc
    private val layoutGravity: Int,                             // vị trí
) : Dialog(context) {

    companion object{
        const val GRAVITY_TOP: Int = Gravity.TOP
        const val GRAVITY_CENTER: Int = Gravity.CENTER
        const val GRAVITY_BOTTOM: Int = Gravity.BOTTOM
    }

    val viewsDialog get () = binding

    override fun onCreate(savedInstanceState: Bundle?) {
        this.setContentView(binding.root)

        // get size screen
        val displayMetrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        // border radius
        if (isBorderRadius) binding.root.setBackgroundResource(R.drawable.background_border_radius_dialog)
        // trong suốt
        if (isTransparent) this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        // set width height
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(this.window?.attributes)
        layoutParams.width = if (isWidthMatchParent) (width * 0.9).toInt() else WRAP_CONTENT
        layoutParams.height = if (isHeightMatchParent) (height * 0.95).toInt() else WRAP_CONTENT
        // set layout gravity
        layoutParams.gravity = layoutGravity
        if (layoutGravity != GRAVITY_CENTER) layoutParams.y = 100                    // margin theo chiều gravity
        this.window?.attributes = layoutParams

        super.onCreate(savedInstanceState)
    }

    interface IBuilder<VB: ViewBinding>{
        var isBorderRadius: Boolean
        var isWidthMatchParent: Boolean
        var isHeightMatchParent: Boolean
        var isTransparent: Boolean
        var layoutGravity: Int

        fun isBorderRadius(isBorderRadius: Boolean): Builder<VB>
        fun isWidthMatchParent(isWidthMatchParent: Boolean): Builder<VB>
        fun isHeightMatchParent(isHeightMatchParent: Boolean): Builder<VB>
        fun isTransparent(isTransparent: Boolean): Builder<VB>
        fun layoutGravity(gravity: Int): Builder<VB>
        fun build(): PolyDialog<VB>
    }

    class Builder<VB: ViewBinding>(private val context: Context, val binding: VB) : IBuilder<VB>{
        // mặc định nó là như này
        override var isBorderRadius: Boolean = true
        override var isWidthMatchParent: Boolean = true
        override var isHeightMatchParent: Boolean = false
        override var isTransparent: Boolean = true
        override var layoutGravity: Int = GRAVITY_CENTER
        override fun isBorderRadius(isBorderRadius: Boolean): Builder<VB> {
            this.isBorderRadius = isBorderRadius
            return this
        }

        override fun isWidthMatchParent(isWidthMatchParent: Boolean): Builder<VB> {
            this.isWidthMatchParent = isWidthMatchParent
            return this
        }

        override fun isHeightMatchParent(isHeightMatchParent: Boolean): Builder<VB> {
            this.isHeightMatchParent = isHeightMatchParent
            return this
        }

        override fun isTransparent(isTransparent: Boolean): Builder<VB> {
            this.isTransparent = isTransparent
            return this
        }

        override fun layoutGravity(layoutGravity: Int): Builder<VB> {
            this.layoutGravity = layoutGravity
            return this
        }

        override fun build(): PolyDialog<VB> = PolyDialog(context, binding, isBorderRadius, isTransparent, isWidthMatchParent, isHeightMatchParent, layoutGravity)
    }
}


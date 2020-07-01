package com.avallie.widgets

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window

open class BaseDialog(
    private val context: Context,
    private val layout: Int,
    private val dismissable: Boolean = true
) {

    val dialog: Dialog by lazy {
        Dialog(context)
    }

    init {
        dialog.run {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(layout)
            setCanceledOnTouchOutside(dismissable)

            window?.run {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    fun show() = dialog.show()

    fun dismiss() = dialog.dismiss()

    fun isShowing() = dialog.isShowing
}

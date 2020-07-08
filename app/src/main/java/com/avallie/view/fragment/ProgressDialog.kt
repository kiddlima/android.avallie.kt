package com.avallie.view.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.avallie.R

class ProgressDialog(private val context: Context, private val message: String) {

    private val dialog: Dialog by lazy {
        Dialog(context)
    }

    init {
        dialog.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.progress_dialog)
            it.setCancelable(false)
        }

        val window = dialog.window

        window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            findViewById<TextView>(R.id.progress_message)?.text = message
        }
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
package com.avallie.widgets

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView

class SelectionDialog(
    context: Context,
    val title: String,
    val description: String,
    val click: View.OnClickListener
) : BaseDialog(context, R.layout.selection_dialog, true) {

    init {
        dialog.run {
            window?.run {
                findViewById<TextView>(R.id.title).text = title
                findViewById<TextView>(R.id.description).text = description

                findViewById<Button>(R.id.cancel_button).setOnClickListener {
                    dismiss()
                }

                findViewById<Button>(R.id.confirm_button).setOnClickListener {
                    click.onClick(it)
                    dismiss()
                }
            }
        }
    }
}

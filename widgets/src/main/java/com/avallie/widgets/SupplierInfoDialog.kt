package com.avallie.widgets

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.TextView

class SupplierInfoDialog(
    context: Activity,
    supplierName: String,
    phoneNumber: String,
    mail: String,
    onPhoneClicked: View.OnClickListener,
    onMailClicked: View.OnClickListener
) : BaseDialog(context, R.layout.supplier_info_dialog, true) {

    init {
        dialog.run {
            window?.run {

                findViewById<TextView>(R.id.title).text = supplierName

                findViewById<Button>(R.id.phone_button).run {
                    setOnClickListener {
                        onPhoneClicked.onClick(it)
                    }

                    text = phoneNumber
                }

                findViewById<Button>(R.id.mail_button).run {
                    setOnClickListener {
                        onMailClicked.onClick(it)
//
                    }

                    text = mail
                }
            }
        }
    }
}
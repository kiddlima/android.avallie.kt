package com.avallie.view.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.avallie.R
import com.avallie.helpers.AppHelper
import com.avallie.helpers.PaperHelper.Companion.getPhases
import com.avallie.helpers.PaperHelper.Companion.addProduct
import com.avallie.model.Product
import com.avallie.model.SelectedProduct
import com.avallie.view.MainActivity
import com.google.android.material.textfield.TextInputEditText


class AddProductDialog(private val context: Context, private val product: Product) {

    val dialog: Dialog by lazy {
        Dialog(context)
    }

    var btnAddProduct: Button? = null
    var quantity: TextInputEditText? = null
    var observations: TextInputEditText? = null

    fun showDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.avallie.R.layout.product_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = dialog.window

        btnAddProduct = window?.findViewById(R.id.add_selected_product)!!
        quantity = window.findViewById(R.id.selected_product_quantity)!!
        observations = window.findViewById(R.id.selected_product_specs)!!

        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        window.findViewById<TextView>(R.id.product_name)?.text = product.name
        window.findViewById<TextView>(R.id.product_specs)?.text = product.category

        window.findViewById<ImageView>(R.id.phase_icon)?.setImageResource(
            context.resources.getIdentifier(
                "ic_phase_" + AppHelper.getPhaseByCategory(
                    product.category, getPhases()
                ), "drawable", context.packageName
            )
        )

        btnAddProduct?.setOnClickListener {
            if (hasValidField(quantity) && hasValidField(observations)) {
                addProduct(SelectedProduct(quantity?.text.toString().toInt(), observations?.text.toString(), product))

                (context as MainActivity).updateCartBadge()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun hasValidField(field: TextInputEditText?): Boolean {
        if (field?.text?.length == 0) {
            field.error = "Campo obrigatório"

            return false
        } else {
            return true
        }
    }
}
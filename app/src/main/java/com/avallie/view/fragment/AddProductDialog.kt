package com.avallie.view.fragment

import android.app.Dialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.avallie.helpers.AppHelper
import com.avallie.helpers.PaperHelper.Companion.addProduct
import com.avallie.helpers.PaperHelper.Companion.getPhases
import com.avallie.model.Product
import com.avallie.model.request.SelectedProduct
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

        btnAddProduct = window?.findViewById(com.avallie.R.id.add_selected_product)!!
        quantity = window.findViewById(com.avallie.R.id.selected_product_quantity)!!
        observations = window.findViewById(com.avallie.R.id.selected_product_specs)!!

        observations!!.setMultiLineCapSentencesAndDoneAction()

        window.findViewById<ConstraintLayout>(com.avallie.R.id.dialog_view).setOnTouchListener { v, event ->
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(dialog.currentFocus.windowToken, 0)
        }



        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        window.findViewById<TextView>(com.avallie.R.id.product_name)?.text = product.name.toLowerCase().capitalize()
        window.findViewById<TextView>(com.avallie.R.id.product_specs)?.text = product.category

        window.findViewById<ImageView>(com.avallie.R.id.phase_icon)?.setImageResource(
                context.resources.getIdentifier(
                        "ic_phase_" + AppHelper.getPhaseByCategory(
                                product.category, getPhases()
                        ), "drawable", context.packageName
                )
        )

//        btnAddProduct?.setOnClickListener {
//            if (hasValidField(quantity) && hasValidField(observations)) {
//                addProduct(
//                        SelectedProduct(
//                                quantity?.text.toString().toInt(),
//                                observations?.text.toString(),
//                                product.id,
//                                product
//                        )
//                )
//
//                (context as MainActivity).updateCartBadge()
//
//                dialog.dismiss()
//            }
//        }

        dialog.show()
    }

    fun EditText.setMultiLineCapSentencesAndDoneAction() {
        imeOptions = EditorInfo.IME_ACTION_DONE
        setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
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
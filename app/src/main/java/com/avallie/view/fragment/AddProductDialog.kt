package com.avallie.view.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.avallie.R
import com.avallie.helpers.AppHelper
import com.avallie.helpers.PaperHelper.Companion.getPhases
import com.avallie.model.Product


class AddProductDialog(private val context: Context, private val product: Product) {

    val dialog: Dialog by lazy {
        Dialog(context)
    }

    fun showDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.avallie.R.layout.product_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        val window = dialog.window

        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        window?.findViewById<TextView>(R.id.product_name)?.text = product.name
        window?.findViewById<TextView>(R.id.product_specs)?.text = product.category

        window?.findViewById<ImageView>(R.id.phase_icon)?.setImageResource(
            context.resources.getIdentifier(
                "ic_phase_" + AppHelper.getPhaseByCategory(
                    product.category, getPhases()
                ), "drawable", context.packageName
            )
        )

        dialog.show()
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setStyle(DialogFragment.STYLE_NORMAL, com.avallie.R.style.FullScreenDialogStyle)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(com.avallie.R.layout.product_dialog, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        product = arguments?.getSerializable("product") as Product?
//
//        product_name.text = product?.name
//        product_specs.text = product?.category
//
//        phase_icon.setImageResource(
//                context!!.resources.getIdentifier(
//                        "ic_phase_" + AppHelper.getPhaseByCategory(
//                                product?.category, getPhases()
//                        ), "drawable", context!!.packageName
//                )
//        )
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        if (dialog != null) {
//            val width = ViewGroup.LayoutParams.MATCH_PARENT
//            val height = ViewGroup.LayoutParams.MATCH_PARENT
//            dialog.window.setLayout(width, height)
//        }
//    }
}
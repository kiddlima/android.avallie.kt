package com.avallie.view.addProduct

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.avallie.R
import com.avallie.generated.callback.OnClickListener
import com.avallie.helpers.PaperHelper
import com.avallie.model.Product
import com.avallie.model.Spec
import com.avallie.model.request.SelectedProduct
import com.avallie.view.MainActivity
import com.avallie.widgets.SelectionDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.redmadrobot.inputmask.MaskedTextChangedListener
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.add_product_fragment.*
import java.lang.Exception
import java.lang.NumberFormatException


class AddProductFragment : BottomSheetDialogFragment() {

    var specsList = mutableListOf<Spec>()

    var product: Product? = null

    var selectedProduct: SelectedProduct? = null

    var inputs = mutableListOf<TextInputLayout>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_product_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }

        product = arguments?.getSerializable("product") as Product?
        selectedProduct = arguments?.getSerializable("selected_product") as SelectedProduct?

        if (product != null) {
            add_product_name.text = product!!.name.toLowerCase().capitalize()
            amount_container.hint = "Quantidade (${product!!.unit})"

            mapSpecs(product!!.specification!!)

        } else {
            mapSpecsAnswered(selectedProduct?.specifications!!)

            add_product_name.text = selectedProduct!!.product.name.toLowerCase().capitalize()
            amount_container.hint = "Quantidade (${selectedProduct?.product!!.unit})"
            amount.setText(selectedProduct?.amount.toString())
            brand.setText(selectedProduct?.brand)
            add_product.text = getString(R.string.update_product)
        }

        val specification =
            if (product != null) product?.specification else selectedProduct?.specifications

        if (specification.equals("N/A")) {
            addSpecView()
        } else {
            for (spec in specsList) {
                val inputContainer =
                    layoutInflater.inflate(
                        R.layout.add_product_edittext,
                        null
                    ) as TextInputLayout

                inputContainer.hint = spec.question

                if (spec.question == "Especificações") {
                    specs_title.visibility = View.GONE
                }

                spec.answer?.run {
                    inputContainer.editText?.setText(this)
                }

                inputs.add(inputContainer)

                specs_container.addView(inputContainer)
            }
        }

        inputs.last().editText!!.setMultiLineCapSentencesAndDoneAction()

        add_product.setOnClickListener {
            if (!amount.text.isNullOrBlank()) {
                if (haveAnyEmptySpecs()) {
                    SelectionDialog(context!!,
                        getString(R.string.confirm_add_product_title),
                        getString(
                            R.string.confirm_add_product_description
                        ),
                        View.OnClickListener {
                            try {
                                saveProduct()

                                (activity as MainActivity).updateCartBadge()

                                dismiss()
                            } catch (e: NumberFormatException) {
                                Toast.makeText(
                                    context,
                                    "Número inválido para quantidade",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    ).show()
                }
            } else {
                Toast.makeText(context, "Informe a quantidade do produto", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun haveAnyEmptySpecs(): Boolean {
        for (input in inputs) {
            if (input.editText?.text.isNullOrBlank()) {
                return true
            }
        }

        return false
    }

    private fun EditText.setMultiLineCapSentencesAndDoneAction() {
        imeOptions = EditorInfo.IME_ACTION_DONE
        setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
    }

    private fun addSpecView() {
        specs_title.visibility = View.GONE

        val inputContainer =
            layoutInflater.inflate(R.layout.add_product_edittext, null) as TextInputLayout

        inputContainer.hint = "Especificações"

        inputs.add(inputContainer)

        specs_container.addView(inputContainer)
    }

    private fun saveProduct() {
        if (product != null) {
            PaperHelper.addProduct(
                SelectedProduct(
                    amount?.text.toString().toDouble(),
                    getFormattedSpecs(),
                    product!!.id,
                    if (!brand.text.isNullOrEmpty()) brand.text.toString() else null,
                    product!!
                )
            )
        } else {
            PaperHelper.updateProduct(
                SelectedProduct(
                    amount?.text.toString().toDouble(),
                    getFormattedSpecs(),
                    selectedProduct?.product?.id!!,
                    if (!brand.text.isNullOrEmpty()) brand.text.toString() else null,
                    selectedProduct?.product!!
                )
            )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (product == null) {
            (activity as MainActivity).openCartSheet()
        }
    }

    private fun getFormattedSpecs(): String {
        var formattedSpecs = ""

        specsList.forEachIndexed { index, spec ->
            formattedSpecs = if (index == 0) {
                "${spec.question}:${inputs[index].editText?.text}"
            } else {
                "${formattedSpecs};${spec.question}:${inputs[index].editText?.text}"
            }
        }

        return formattedSpecs
    }

    private fun mapSpecs(specs: String) {
        val stringSpecsArray = specs.split("; ").toTypedArray()

        for (stringSpec in stringSpecsArray) {
            val spec = Spec()

            if (stringSpec == "N/A") {
                spec.question = "Especificações"
            } else {
                spec.question = stringSpec
            }

            specsList.add(spec)
        }
    }

    private fun mapSpecsAnswered(specs: String) {
        val stringSpecsArray = specs.split(";").toTypedArray()

        for (stringSpec in stringSpecsArray) {
            val spec = Spec()

            val splittedSpec = stringSpec.split(":").toTypedArray()

            spec.question = splittedSpec[0]
            spec.answer = splittedSpec[1]

            specsList.add(spec)
        }
    }
}
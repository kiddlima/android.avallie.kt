package com.avallie.view.address

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avallie.R
import com.avallie.view.address.model.Address
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.confirm_address_fragment.*

class ConfirmAddressBottomSheet : BottomSheetDialogFragment() {

    private val address by lazy {
        arguments?.getSerializable("address") as Address
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.confirm_address_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        address.run {
            if (hasNumber()) {
                address_title.text =
                    "$street, $streetNumber\n${if (district != null) "$district, " else ""}$state - $country "

                number_container.visibility = View.GONE
            } else {
                address_title.text =
                    "$street\n${if (district != null) "$district, " else ""}$state - $country "
            }
        }

        number_container.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                number_container.editText?.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        confirm_address_button.setOnClickListener {
            if (!address.hasNumber()) {
                if (number_container.editText?.text.isNullOrEmpty()) {
                    number_container.error = "Informe o número"
                } else {
                    confirmAddress()
                }
            } else {
                confirmAddress()
            }
        }
    }

    private fun confirmAddress() {
        address.streetNumber = number_container.editText?.text.toString()
        address.additionalAddress = additional_address_container.editText?.text.toString()

        //TODO register new address on api

        activity?.finish()
    }
}
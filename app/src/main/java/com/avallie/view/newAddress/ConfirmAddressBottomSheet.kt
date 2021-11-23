package com.avallie.view.newAddress

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.avallie.R
import com.avallie.helpers.PaperHelper
import com.avallie.model.ScreenState
import com.avallie.view.newAddress.model.Address
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.confirm_address_fragment.*

class ConfirmAddressBottomSheet : BottomSheetDialogFragment() {

    private val address by lazy {
        arguments?.getSerializable("address") as Address
    }

    lateinit var viewModel: FindAddressViewModel

    var hadNumber: Boolean = false

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

            hadNumber = hasNumber()
        }

        viewModel = ViewModelProviders.of(this).get(FindAddressViewModel::class.java)

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
                    address.streetNumber = number_container.editText?.text.toString().toInt()
                    address.additionalAddress =
                        additional_address_container.editText?.text.toString()

                    confirmAddress()
                }
            } else {
                address.additionalAddress = additional_address_container.editText?.text.toString()

                confirmAddress()
            }
        }
    }

    private fun confirmAddress() {
        setLoading()

        if (address.additionalAddress?.isEmpty()!!) {
            address.additionalAddress = null
        }

        if (hadNumber) {
            sendAddress()
        } else {
            viewModel.searchPlaces(
                "${address.street} ${address.streetNumber} ${address.city} ${address.state} ${address.country}",
                context!!,
                object : FindAddressViewModel.OnPredictionSearch {
                    override fun onFind(prediction: AutocompletePrediction) {
                        viewModel.findPlace(
                            prediction.placeId,
                            context!!,
                            object : FindAddressViewModel.OnPlaceSearch {
                                override fun onFind(address: Address) {
                                    this@ConfirmAddressBottomSheet.address.postalCode =
                                        address.postalCode


                                    sendAddress()
                                }
                            })
                    }
                }
            )
        }
    }

    private fun sendAddress() {
        HttpService(context!!).addAddress(address, object : ConnectionListener<Address> {
            override fun onSuccess(response: Address) {
                PaperHelper.setDefaultAddress(response)

                activity?.setResult(1)
                activity?.finish()
            }

            override fun onFail(error: String?) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()

                removeLoading()
            }

            override fun noInternet() {
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                removeLoading()
            }
        })
    }

    private fun setLoading() {
        confirm_address_button.isEnabled = false
        confirm_address_button.text = ""

        progress_bar.visibility = View.VISIBLE
    }

    private fun removeLoading() {
        confirm_address_button.isEnabled = true
        confirm_address_button.text = getString(R.string.confirm_address)

        progress_bar.visibility = View.GONE
    }
}
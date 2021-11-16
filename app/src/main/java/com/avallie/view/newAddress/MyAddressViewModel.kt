package com.avallie.view.newAddress

import android.content.Context
import com.avallie.core.CustomViewModel
import com.avallie.helpers.PaperHelper
import com.avallie.model.Customer
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class MyAddressViewModel : CustomViewModel() {

    val customer by lazy {
        PaperHelper.getCustomer()
    }

    fun getCustomer(context: Context) {
        mState.value = ScreenState.Loading

        HttpService(context).getCustomer(object : ConnectionListener<Customer> {
            override fun onSuccess(response: Customer) {
                mState.value = ScreenState.Success
            }

            override fun onFail(error: String?) {
                mState.value = ScreenState.Fail
            }

            override fun noInternet() {
                mState.value = ScreenState.Fail
            }
        })
    }
}
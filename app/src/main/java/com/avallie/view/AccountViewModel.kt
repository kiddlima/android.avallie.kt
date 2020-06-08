package com.avallie.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.helpers.PaperHelper
import com.avallie.model.Customer
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class AccountViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()

    val customer = MutableLiveData<Customer>()

    val screenState = MutableLiveData<ScreenState>()

    init {
        customer.value = PaperHelper.getCustomer()

        if (customer.value == null){
            screenState.value = ScreenState.Loading
        } else {
            screenState.value = ScreenState.Success
        }
    }

    fun getCustomer(context: Context) {
        HttpService(context).getCustomer(object : ConnectionListener<Customer> {
            override fun onSuccess(response: Customer) {
                customer.value = response

                PaperHelper.saveCustomer(customer.value!!)

                screenState.value = ScreenState.Success
            }

            override fun onFail(error: String?) {
                if (customer.value == null) {
                    errorMessage.value = error

                    screenState.value = ScreenState.Fail
                }
            }

            override fun noInternet() {
                if (customer.value == null) {
                    errorMessage.value = context.getString(R.string.no_connection)

                    screenState.value = ScreenState.Fail
                }
            }
        })
    }

}
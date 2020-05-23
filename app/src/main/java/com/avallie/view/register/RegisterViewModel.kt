package com.avallie.view.register

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.model.Customer
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class RegisterViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val registerScreen = MutableLiveData<RegisterScreen>()

    val screenState = MutableLiveData<ScreenState>()

    val customer = MutableLiveData<Customer>()
    
    init {
        customer.value = Customer()
    }

    fun register(context: Context) {
        screenState.value = ScreenState.Loading

        HttpService(context).registerCustomer(customer.value!!, object : ConnectionListener<Any> {
            override fun onSuccess(response: Any) {
                screenState.value = ScreenState.Success
            }

            override fun onFail(error: String?) {
                errorMessage.value = error
                screenState.value = ScreenState.Fail
            }

            override fun noInternet() {
                errorMessage.value = context.resources.getString(R.string.no_connection)
                screenState.value = ScreenState.Fail
            }
        })
    }

}
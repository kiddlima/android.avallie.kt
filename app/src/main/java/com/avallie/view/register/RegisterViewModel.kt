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

    val validCpf = MutableLiveData<Boolean>()
    val validEmail = MutableLiveData<Boolean>()

    val validateCpfState = MutableLiveData<ScreenState>()
    val validateEmailState = MutableLiveData<ScreenState>()

    init {
        customer.value = Customer()
    }

    private fun clearCpf() {
        customer.value!!.cpf = customer.value!!.cpf?.replace("-", "")
        customer.value!!.cpf = customer.value!!.cpf?.replace(".", "")
    }

    fun validateCpf(context: Context) {
        clearCpf()

        validateCpfState.value = ScreenState.Loading

        HttpService(context).validateCpf(
            customer.value!!.cpf!!,
            object : ConnectionListener<Boolean> {
                override fun onSuccess(response: Boolean) {
                    validateCpfState.value = ScreenState.Success

                    validCpf.value = response
                }

                override fun onFail(error: String?) {
                    validateCpfState.value = ScreenState.Fail

                    validCpf.value = false
                }

                override fun noInternet() {
                    validateCpfState.value = ScreenState.Fail
                    validCpf.value = false
                }

            })
    }

    fun validateEmail(context: Context) {
        validateEmailState.value = ScreenState.Loading

        HttpService(context).validateEmail(
            customer.value!!.email!!,
            object : ConnectionListener<Boolean> {
                override fun onSuccess(response: Boolean) {
                    validateEmailState.value = ScreenState.Success

                    validEmail.value = response
                }

                override fun onFail(error: String?) {
                    validateEmailState.value = ScreenState.Fail

                    validEmail.value = false
                }

                override fun noInternet() {
                    validateEmailState.value = ScreenState.Fail

                    validEmail.value = false
                }

            })
    }

    fun register(context: Context) {
        customer.value!!.telephone = customer.value!!.telephone?.replace("-", "")
        customer.value!!.telephone = customer.value!!.telephone?.replace("(", "")
        customer.value!!.telephone = customer.value!!.telephone?.replace(")", "")
        clearCpf()
        customer.value!!.zipCode = customer.value!!.zipCode?.replace("-", "")

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
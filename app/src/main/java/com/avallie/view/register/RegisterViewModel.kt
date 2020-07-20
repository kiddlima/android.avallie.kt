package com.avallie.view.register

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.helpers.AuthHelper
import com.avallie.model.Customer
import com.avallie.model.OfficialAddress
import com.avallie.model.ScreenState
import com.avallie.webservice.CepHttp
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

    val cepLoading = MutableLiveData<ScreenState>()

    var fromNextClickedCpf: Boolean? = false
    var fromNextClickedEmail: Boolean? = false

    init {
        customer.value = Customer()
    }

    private fun clearCpf() {
        customer.value!!.cpf = customer.value!!.cpf?.replace("-", "")
        customer.value!!.cpf = customer.value!!.cpf?.replace(".", "")
    }

    fun validateCpf(context: Context, fromNextClicked: Boolean? = false) {
        clearCpf()

        this.fromNextClickedCpf = fromNextClicked

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

    fun getCepInfo(context: Context) {
        cepLoading.value = ScreenState.Loading

        CepHttp(context).getCepInfo(
            customer.value?.zipCode?.replace("-", "")!!,
            object : ConnectionListener<OfficialAddress> {
                override fun onSuccess(response: OfficialAddress) {
                    customer.value?.run {
                        city = response.localidade
                        state = response.uf
                        street = response.logradouro
                    }

                    cepLoading.value = ScreenState.Success
                }

                override fun onFail(error: String?) {
                    cepLoading.value = ScreenState.Fail
                }

                override fun noInternet() {
                    cepLoading.value = ScreenState.Fail
                }
            })
    }

    fun validateEmail(context: Context, fromNextClickedEmail: Boolean? = false) {
        validateEmailState.value = ScreenState.Loading
        
        this.fromNextClickedEmail = fromNextClickedEmail

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
                login(context)
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

    fun login(context: Context){
        AuthHelper.login(customer.value?.email!!, customer.value?.password!!, context, object: ConnectionListener<String> {
            override fun onSuccess(response: String) {
                screenState.value = ScreenState.Success
            }

            override fun onFail(error: String?) {
                screenState.value = ScreenState.Success
            }

            override fun noInternet() {
                screenState.value = ScreenState.Success
            }
        })
    }

}
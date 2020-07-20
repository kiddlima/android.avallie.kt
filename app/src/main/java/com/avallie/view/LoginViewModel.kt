package com.avallie.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.helpers.AuthHelper
import com.avallie.model.Customer
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService
import com.google.firebase.auth.FirebaseAuth
import javax.xml.transform.Templates

class LoginViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    val screenState = MutableLiveData<ScreenState>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String, context: Context) {
        auth = FirebaseAuth.getInstance()

        screenState.value = ScreenState.Loading

        AuthHelper.login(email, password, context, object : ConnectionListener<String> {
            override fun onSuccess(response: String) {
                screenState.value = ScreenState.Success
            }

            override fun onFail(error: String?) {
                errorMessage.value = error

                screenState.value = ScreenState.Fail
            }

            override fun noInternet() {
                errorMessage.value = context.getString(R.string.server_error)

                screenState.value = ScreenState.Fail
            }
        })
    }
}
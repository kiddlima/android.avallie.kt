package com.avallie.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.helpers.AuthHelper
import com.avallie.model.Customer
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import javax.xml.transform.Templates

class LoginViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    val screenState = MutableLiveData<ScreenState>()
    val errorMessage = MutableLiveData<String>()

    fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    fun login(email: String, password: String, context: Context) {
        auth = FirebaseAuth.getInstance()

        screenState.value = ScreenState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    auth.currentUser?.getIdToken(false)?.addOnCompleteListener {

                        if (it.result?.claims?.get("role") == "SUPPLIER") {
                            auth.signOut()

                            screenState.value = ScreenState.Fail
                            errorMessage.value =
                                "Para ter acesso as suas informações você deve acessar o portal web."
                        } else {
                            HttpService(context).setNotificationToken(null)

                            HttpService(context).getCustomer(object : ConnectionListener<Customer> {
                                override fun onSuccess(response: Customer) {
                                    screenState.value = ScreenState.Success
                                }

                                override fun onFail(error: String?) {
                                    errorMessage.value = "Falha ao recuperar dados cadastrais"

                                    AuthHelper.logout()

                                    screenState.value = ScreenState.Fail
                                }

                                override fun noInternet() {
                                    errorMessage.value = "Sem conexão com a internet"

                                    AuthHelper.logout()

                                    screenState.value = ScreenState.Fail
                                }

                            })
                        }
                    }

                } else {
                    errorMessage.value = "Usuário ou senha incorretos"

                    screenState.value = ScreenState.Fail
                }
            }
    }
}
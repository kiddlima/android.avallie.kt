package com.avallie.helpers

import android.content.Context
import com.avallie.model.Customer
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService
import com.google.firebase.auth.FirebaseAuth

class AuthHelper {

    companion object {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()

        fun isLoggedIn(): Boolean {
            return auth.currentUser != null;
        }

        fun logout() {
            auth.signOut()
        }

        fun login(
            email: String,
            password: String,
            context: Context,
            connectionListener: ConnectionListener<String>
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        auth.currentUser?.getIdToken(false)?.addOnCompleteListener {

                            if (it.result?.claims?.get("role") == "SUPPLIER") {
                                auth.signOut()

                                connectionListener.onFail("Para ter acesso as suas informações você deve acessar o portal web.")
                            } else {
                                HttpService(context).setNotificationToken(null)

                                HttpService(context).getCustomer(object :
                                    ConnectionListener<Customer> {
                                    override fun onSuccess(response: Customer) {
                                        connectionListener.onSuccess("Login realizado com sucesso")
                                    }

                                    override fun onFail(error: String?) {
                                        logout()

                                        connectionListener.onFail("Falha ao recuperar dados cadastrais")
                                    }

                                    override fun noInternet() {
                                        logout()
                                        connectionListener.onFail("Sem conexão com a internet")
                                    }

                                })
                            }
                        }

                    } else {
                        connectionListener.onFail("Usuário ou senha incorretos")
                    }
                }
        }
    }

}
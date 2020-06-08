package com.avallie.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.model.ScreenState
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    val screenState = MutableLiveData<ScreenState>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String) {
        auth = FirebaseAuth.getInstance()

        screenState.value = ScreenState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    auth.currentUser?.getIdToken(true)?.addOnCompleteListener {

                        if (it.result?.claims?.get("role") == "SUPPLIER") {
                            auth.signOut()

                            screenState.value = ScreenState.Fail
                            errorMessage.value = "Para ter acesso as suas informações você deve acessar o portal web."
                        } else {
                            screenState.value = ScreenState.Success
                            it.result?.token
                        }
                    }

                } else {
                    errorMessage.value = "Usuário ou senha incorretos"

                    screenState.value = ScreenState.Fail
                }
            }
    }
}
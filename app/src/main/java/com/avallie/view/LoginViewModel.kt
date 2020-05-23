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
                    screenState.value = ScreenState.Success

                    auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
                        it.result?.token
                    }

                } else {
                    errorMessage.value = "Usuário ou senha incorretos"

                    screenState.value = ScreenState.Fail
                }
            }
    }
}
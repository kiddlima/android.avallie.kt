package com.avallie.helpers

import com.google.firebase.auth.FirebaseAuth

class AuthHelper {

    companion object {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        fun isLoggedIn(): Boolean {
            return auth.currentUser != null;
        }

        fun logout() {
            auth.signOut()
        }
    }

}
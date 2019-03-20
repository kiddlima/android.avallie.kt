package com.avallie.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avallie.R
import com.avallie.view.fragment.ProgressDialog
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        back_button.setOnClickListener {
            finish()
        }

        btn_login.setOnClickListener {
            ProgressDialog(this, "Autenticando seu usuário, por favor aguarde.").show()
        }
    }
}

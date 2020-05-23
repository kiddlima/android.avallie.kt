package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.avallie.R
import com.avallie.helpers.AppHelper.Companion.getErrorSnackbar
import com.avallie.model.ScreenState
import com.avallie.view.fragment.ProgressDialog
import com.avallie.view.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this, "Autenticando seus dados, por favor aguarde.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        back_button.setOnClickListener {
            finish()
        }

        btn_login.setOnClickListener {
            viewModel.login(login_email.text.toString(), login_password.text.toString())
        }

        create_account.setOnClickListener {
            Intent(this, RegisterActivity::class.java).run {
                startActivity(this)
            }
        }

        viewModel.errorMessage.observe(this, Observer {
            showErrorMessage(it)
        })

        viewModel.screenState.observe(this, Observer {
            if (it == ScreenState.Loading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }

            if (it == ScreenState.Success) {
                finish()

                progressDialog.dismiss()
            }
        })
    }

    private fun showErrorMessage(errorMessage: String) {
        getErrorSnackbar(this, root_layout, errorMessage).show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

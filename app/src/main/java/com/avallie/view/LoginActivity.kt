package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.avallie.R
import com.avallie.helpers.AppHelper.Companion.getErrorSnackbar
import com.avallie.model.ScreenState
import com.avallie.view.fragment.ProgressDialog
import com.avallie.view.register.RegisterActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
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

        scroll_view.setOnTouchListener { v, event ->
            if (currentFocus != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            }

            false
        }

        btn_google.setOnClickListener {

        }

        btn_login.setOnClickListener {
            if (login_email.text.isNullOrBlank() || login_password.text.isNullOrBlank()) {
                Toast.makeText(this, "Preencha as informações de login", Toast.LENGTH_SHORT).show()
            } else {
                val googleAPI = GoogleApiAvailability.getInstance()
                val result = googleAPI.isGooglePlayServicesAvailable(this)

                if (result != ConnectionResult.SUCCESS) {
                    if (googleAPI.isUserResolvableError(result)) {
                        //prompt the dialog to update google play
                        googleAPI.getErrorDialog(this, result, 9000).show()
                    }
                } else {
                    viewModel.login(
                        login_email.text.toString(),
                        login_password.text.toString(),
                        this
                    )
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.login(
            login_email.text.toString(),
            login_password.text.toString(),
            this
        )
    }

    private fun showErrorMessage(errorMessage: String) {
        getErrorSnackbar(this, root_layout, errorMessage).show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

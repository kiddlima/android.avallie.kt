package com.avallie.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avallie.R
import com.avallie.view.fragment.ProgressDialog
import kotlinx.android.synthetic.main.activity_register.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class RegisterActivity : AppCompatActivity() {

    enum class RegisterScreen {
        FIRST,
        SECOND,
        THIRD
    }

    private var currentScreen: RegisterScreen = RegisterScreen.FIRST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_next.setOnClickListener {
            when (currentScreen) {
                RegisterScreen.FIRST -> {
                    if (isFirstScreenValidate()) {
                        goToSecondScreen()
                    } else {
                        showError(getString(R.string.fill_the_fileds))
                    }
                }
                RegisterScreen.SECOND -> {
                    if (isSecondScreenValidate()) {
                        goToThridScreen()
                    } else {
                        showError(getString(R.string.fill_the_fileds))
                    }
                }
                else -> {
                    if (isThirdScreenValidate()) {
                        finishRegister()
                    } else {
                        showError(getString(R.string.fill_the_fileds))
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun isFirstScreenValidate(): Boolean {
        return !register_name.text.isNullOrBlank() && !register_cpf.text.isNullOrBlank() && !register_phone.text.isNullOrBlank()
    }

    private fun isSecondScreenValidate(): Boolean {
        return !register_company.text.isNullOrBlank() && !register_cep.text.isNullOrBlank() && !register_street.text.isNullOrBlank() && !register_number.text.isNullOrBlank() && !register_city.text.isNullOrBlank() && !register_state.text.isNullOrBlank()
    }

    private fun isThirdScreenValidate(): Boolean {
        return if (!register_password.text.isNullOrBlank() && !register_confirm_password.text.isNullOrBlank()) {
            if (register_password.text.toString() != register_confirm_password.text.toString()) {
                showError(getString(R.string.passwords_not_matching))
                false
            } else {
                !register_email.text.isNullOrBlank()
            }
        } else {
            false
        }
    }

    private fun goToSecondScreen() {
        register_first_screen.visibility = View.GONE
        register_second_screen.visibility = View.VISIBLE
        register_third_screen.visibility = View.GONE

        currentScreen = RegisterScreen.SECOND
        register_subtitle.text = getString(R.string.company_info)
    }

    private fun goToThridScreen() {
        register_first_screen.visibility = View.GONE
        register_second_screen.visibility = View.GONE
        register_third_screen.visibility = View.VISIBLE

        currentScreen = RegisterScreen.THIRD
        register_subtitle.text = getString(R.string.access_info)
    }

    private fun finishRegister() {
        ProgressDialog(this, getString(R.string.finishing_register)).show()
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}

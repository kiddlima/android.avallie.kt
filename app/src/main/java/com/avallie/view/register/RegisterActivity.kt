package com.avallie.view.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.avallie.R
import com.avallie.databinding.ActivityRegisterBinding
import com.avallie.helpers.AppHelper
import com.avallie.model.ScreenState
import com.avallie.view.fragment.ProgressDialog
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.register_tracking.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel

    lateinit var binding: ActivityRegisterBinding

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.model = this
        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        binding.viewModel = viewModel

        progressDialog = ProgressDialog(this, getString(R.string.finishing_register))

        goToFirstScreen()

        viewModel.screenState.observe(this, Observer {
            when (it) {
                ScreenState.Loading -> progressDialog.show()
                ScreenState.Success -> {
                    startActivity(Intent(this, RegisterSuccessActivity::class.java))
                    finish()
                }
                ScreenState.Fail -> {
                    progressDialog.dismiss()
                    showRequestError(viewModel.errorMessage.value!!)
                }
            }
        })

        btn_next.setOnClickListener {
            when (viewModel.registerScreen.value) {
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

        first_step_circle.setOnClickListener {
            goToFirstScreen()
        }

        second_step_circle.setOnClickListener {
            if (isFirstScreenValidate()) {
                goToSecondScreen()
            }
        }

        thrid_step_circle.setOnClickListener {
            if (isThirdScreenValidate() || (isFirstScreenValidate() && isSecondScreenValidate())) {
                goToThridScreen()
            }
        }
    }

    private fun isFirstScreenValidate(): Boolean {
        return !binding.registerName.text.isNullOrBlank() && !binding.registerCpf.text.isNullOrBlank() && !binding.registerPhone.text.isNullOrBlank()
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

    private fun goToFirstScreen() {
        viewModel.registerScreen.value = RegisterScreen.FIRST
        register_subtitle.text = getString(R.string.personal_info)

        updateTracking()
    }

    private fun goToSecondScreen() {
        viewModel.registerScreen.value = RegisterScreen.SECOND
        register_subtitle.text = getString(R.string.company_info)

        updateTracking()
    }

    private fun goToThridScreen() {
        viewModel.registerScreen.value = RegisterScreen.THIRD
        register_subtitle.text = getString(R.string.access_info)

        updateTracking()
    }

    private fun updateTracking() {
        val accentDrawable = ContextCompat.getDrawable(this, R.drawable.accent_circle)
        val grayDrawable = ContextCompat.getDrawable(this, R.drawable.gray_circle)
        val accentColor = ContextCompat.getColor(this, R.color.colorAccent)
        val grayColor = ContextCompat.getColor(this, R.color.grayPrimary)

        when (viewModel.registerScreen.value) {
            RegisterScreen.FIRST -> {
                first_step_line.setBackgroundColor(grayColor)
                second_step_line.setBackgroundColor(grayColor)

                thrid_step_circle.background = grayDrawable
                second_step_circle.background = grayDrawable
                first_step_circle.background = accentDrawable
            }
            RegisterScreen.SECOND -> {
                first_step_line.setBackgroundColor(accentColor)
                second_step_line.setBackgroundColor(grayColor)

                thrid_step_circle.background = grayDrawable
                second_step_circle.background = accentDrawable

            }
            else -> {
                thrid_step_circle.background = accentDrawable
                second_step_line.setBackgroundColor(accentColor)
            }
        }
    }

    private fun finishRegister() {
        viewModel.register(this)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showRequestError(errorMessage: String) {
        AppHelper.getErrorSnackbar(this, register_root, errorMessage).show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

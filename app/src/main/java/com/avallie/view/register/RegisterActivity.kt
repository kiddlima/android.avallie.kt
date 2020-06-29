package com.avallie.view.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.redmadrobot.inputmask.MaskedTextChangedListener
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

        register_cpf.run {
            val listener = MaskedTextChangedListener("[000]{.}[000]{.}[000]{-}[00]", register_cpf)

            addTextChangedListener(listener)
            onFocusChangeListener = listener
        }

        register_cpf.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && register_cpf.text!!.isNotEmpty()) {
                register_cpf.error = null

                viewModel.validateCpf(this)
            }
        }

        viewModel.cepLoading.observe(this, Observer {
            if (it == ScreenState.Success) {
                viewModel.customer.value?.run {
                    binding.registerStreet.setText(street)
                    binding.registerState.setText(state)
                    binding.registerCity.setText(city)
                }
            } else if (it == ScreenState.Fail) {
                showError("Erro ao consultar CEP")
            }
        })

        register_email.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && register_email.text!!.isNotEmpty()) {
                register_email.error = null

                viewModel.validateEmail(this)
            }
        }

        register_phone.run {
            val listener = MaskedTextChangedListener("{(}[00]{)}[00000]{-}[0000]", register_phone)

            addTextChangedListener(listener)
            onFocusChangeListener = listener
        }

        register_cep.run {
            val listener = MaskedTextChangedListener("[00000]{-}[000]", register_cep)

            addTextChangedListener(listener)
            onFocusChangeListener = listener
        }

        register_cep.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && register_cep.text!!.isNotEmpty()) {
                viewModel.getCepInfo(this)
            }
        }

        progressDialog = ProgressDialog(this, getString(R.string.finishing_register))

        goToFirstScreen()

        binding.backButton.setOnClickListener {
            finish()
        }

        viewModel.validCpf.observe(this, Observer {
            if (!it) {
                register_cpf.error = "CPF inválido ou cadastrado"

                Toast.makeText(this, "CPF inválido ou cadastrado", Toast.LENGTH_SHORT).show()
            } else {
                register_cpf.error = null
            }
        })

        viewModel.validEmail.observe(this, Observer {
            if (!it) {
                register_email.error = "Email inválido ou já cadastrado"

                Toast.makeText(this, "Email inválido ou já cadastrado", Toast.LENGTH_SHORT).show()
            } else {
                register_email.error = null
            }
        })

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
        return if (viewModel.validCpf.value == null) {
            viewModel.validateCpf(this)

            false
        } else {
            !binding.registerName.text.isNullOrBlank() && !binding.registerCpf.text.isNullOrBlank() && !binding.registerPhone.text.isNullOrBlank() && viewModel.validCpf.value!!
        }

    }

    private fun isSecondScreenValidate(): Boolean {
        return !register_cep.text.isNullOrBlank() && !register_street.text.isNullOrBlank() && !register_number.text.isNullOrBlank() && !register_city.text.isNullOrBlank() && !register_state.text.isNullOrBlank()
    }

    private fun isThirdScreenValidate(): Boolean {
        if (viewModel.validEmail.value == null) {
            viewModel.validateEmail(this)

            return false
        } else {
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
    }

    private fun goToFirstScreen() {
        viewModel.registerScreen.value = RegisterScreen.FIRST
        register_subtitle.text = getString(R.string.personal_info)

        register_name.requestFocus()

        updateTracking()
    }

    private fun goToSecondScreen() {
        viewModel.registerScreen.value = RegisterScreen.SECOND
        register_subtitle.text = getString(R.string.company_info)

        register_company.requestFocus()

        updateTracking()
    }

    private fun goToThridScreen() {
        viewModel.registerScreen.value = RegisterScreen.THIRD
        register_subtitle.text = getString(R.string.access_info)

        register_email.requestFocus()

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

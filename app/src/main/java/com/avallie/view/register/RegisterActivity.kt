package com.avallie.view.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.google.android.material.textfield.TextInputLayout
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

        binding.registerName.addTextChangedListener(clearError(binding.nameContainer))
        binding.registerCpf.addTextChangedListener(clearError(binding.cpfInputLayout))
        binding.registerPhone.addTextChangedListener(clearError(binding.phoneContainer))
        binding.registerEmail.addTextChangedListener(clearError(binding.emailInputLayout))

        binding.registerConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(
                confirmPassword: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                validateSamePassword()
            }
        })

        binding.registerPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! < 6) {
                    binding.passwordContainer.error = "A senha deve ter 6 ou mais caracteres."
                } else {
                    binding.passwordContainer.error = null

                    if (binding.registerConfirmPassword.text?.isNotEmpty()!!) {
                        validateSamePassword()
                    }
                }
            }
        })


        register_cpf.run {
            val listener = MaskedTextChangedListener("[000]{.}[000]{.}[000]{-}[00]", register_cpf)

            addTextChangedListener(listener)
            onFocusChangeListener = listener
        }

        register_cpf.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && register_cpf.text!!.isNotEmpty()) {
                cpf_input_layout.error = null

                viewModel.validateCpf(this)
            }
        }

//
        register_email.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && register_email.text!!.isNotEmpty()) {
                binding.emailInputLayout.error = null

                viewModel.validateEmail(this)
            }
        }

        register_phone.run {
            val listener = MaskedTextChangedListener("{(}[00]{)}[00000]{-}[0000]", register_phone)

            addTextChangedListener(listener)
            onFocusChangeListener = listener
        }

        progressDialog = ProgressDialog(this, getString(R.string.finishing_register))

        goToFirstScreen()

        binding.backButton.setOnClickListener {
            finish()
        }

        viewModel.validEmail.observe(this, Observer {
            if (!it) {
                email_input_layout.error = "Email inválido ou já cadastrado"
            } else {
                if (viewModel.fromNextClickedEmail!!) {
                    if (isThirdScreenValidate()) {
                        finishRegister()
                    }
                }

                email_input_layout.error = null
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
                        if (register_cpf.isFocused) {
                            viewModel.validateCpf(this, true)
                        } else {
                            goToSecondScreen()
                        }
                    } else {
                        if (register_cpf.isFocused && !register_cpf.text.isNullOrBlank()) {
                            viewModel.validateCpf(this, true)
                        }
                    }
                }
                else -> {
                    if (isThirdScreenValidate()) {
                        finishRegister()
                    }
                }
            }
        }

        first_step_circle.setOnClickListener {
            goToFirstScreen()
        }

        thrid_step_circle.setOnClickListener {
            if (isThirdScreenValidate() || isFirstScreenValidate()) {
                goToSecondScreen()
            }
        }
    }

    private fun validateSamePassword() {
        if (binding.registerConfirmPassword.text.toString() == binding.registerPassword.text.toString()) {
            binding.confirmPasswordContainer.error = null
        } else {
            binding.confirmPasswordContainer.error = "Senhas diferentes"
        }
    }


    private fun clearError(textInputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = null
            }
        }
    }

    private fun isFirstScreenValidate(): Boolean {
        var valid = true

        if (!binding.registerPhone.text?.matches(Regex("^1\\d\\d(\\d\\d)?\$|^0800 ?\\d{3} ?\\d{4}\$|^(\\(0?([1-9a-zA-Z][0-9a-zA-Z])?[1-9]\\d\\) ?|0?([1-9a-zA-Z][0-9a-zA-Z])?[1-9]\\d[ .-]?)?(9|9[ .-])?[2-9]\\d{3}[ .-]?\\d{4}\$"))!!) {
            binding.phoneContainer.error = "Telefone inválido"

            valid = false
        }

        if (binding.registerCpf.text.isNullOrBlank()) {
            binding.cpfInputLayout.error = "CPF obrigatório"

            valid = false
        } else {
            if (viewModel.validCpf.value == null) {
                viewModel.validateCpf(this)

                valid = false
            }
        }

        if (binding.registerName.text.isNullOrBlank()) {
            binding.nameContainer.error = "Nome obrigatório"

            valid = false
        }

        return valid
    }


    private fun isThirdScreenValidate(): Boolean {
        var valid = true

        if (binding.registerEmail.text.isNullOrBlank()) {
            binding.emailInputLayout.error = "Email obrigatório"

            valid = false
        } else {
            if (viewModel.validEmail.value == null) {
                viewModel.validateEmail(this)

                valid = false
            } else {
                if (!viewModel.validEmail.value!!) {
                    valid = false
                }
            }
        }

        if (!register_password.text.isNullOrBlank() && !register_confirm_password.text.isNullOrBlank()) {
            if (register_password.text?.length!! < 6) {
                binding.passwordContainer.error = "A senha deve ter 6 ou mais caracteres."
                valid = false
            }

            if (register_password.text.toString() != register_confirm_password.text.toString()) {
                binding.confirmPasswordContainer.error = "As senhas não são iguais"
                valid = false
            }
        } else {
            binding.passwordContainer.error = "Senha obrigatório"
            valid = false
        }

        return valid
    }

    private fun goToFirstScreen() {
        viewModel.registerScreen.value = RegisterScreen.FIRST
        register_subtitle.text = getString(R.string.personal_info)

        register_name.requestFocus()

        updateTracking()
    }

    private fun goToSecondScreen() {
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

                thrid_step_circle.background = grayDrawable
                first_step_circle.background = accentDrawable
            }

            else -> {
                first_step_line.setBackgroundColor(accentColor)
                thrid_step_circle.background = accentDrawable
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

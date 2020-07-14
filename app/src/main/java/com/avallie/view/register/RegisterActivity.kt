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
import androidx.core.widget.doOnTextChanged
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
                cpf_input_layout.error = null

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
                binding.emailInputLayout.error = null

                viewModel.validateEmail(this)
            }
        }

        register_phone.run {
            val listener = MaskedTextChangedListener("{(}[00]{)}[00000]{-}[0000]", register_phone)

            addTextChangedListener(listener)
            onFocusChangeListener = listener
        }

        register_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.phoneContainer.error = null
            }
        })
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
                cpf_input_layout.error = "CPF inválido ou cadastrado"

                Toast.makeText(this, "CPF inválido ou cadastrado", Toast.LENGTH_SHORT).show()
            } else {
                cpf_input_layout.error = null

                if (viewModel.fromNextClickedCpf!!) {
                    if (isFirstScreenValidate()) {
                        goToSecondScreen()
                    } else {
                        showError(getString(R.string.fill_the_fileds))
                    }
                }
            }
        })

        viewModel.validEmail.observe(this, Observer {
            if (!it) {
                email_input_layout.error = "Email inválido ou já cadastrado"

                Toast.makeText(this, "Email inválido ou já cadastrado", Toast.LENGTH_SHORT).show()
            } else {
                if (viewModel.fromNextClickedEmail!!) {
                    if (isThirdScreenValidate()) {
                        finishRegister()
                    } else {
                        showError(getString(R.string.fill_the_fileds))
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
                        } else {
                            showError(getString(R.string.fill_the_fileds))
                        }
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
                        if (register_email.isFocused) {
                            viewModel.validateEmail(this, true)
                        } else {
                            finishRegister()
                        }
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
        if (!binding.registerPhone.text?.matches(Regex("^1\\d\\d(\\d\\d)?\$|^0800 ?\\d{3} ?\\d{4}\$|^(\\(0?([1-9a-zA-Z][0-9a-zA-Z])?[1-9]\\d\\) ?|0?([1-9a-zA-Z][0-9a-zA-Z])?[1-9]\\d[ .-]?)?(9|9[ .-])?[2-9]\\d{3}[ .-]?\\d{4}\$"))!!) {
            binding.phoneContainer.error = "Telefone inválido"

            return false
        }

        if (binding.registerCpf.text.isNullOrBlank()) {
            return false
        } else {
            if (viewModel.validCpf.value == null) {
                viewModel.validateCpf(this)

                return false
            } else {
                return !binding.registerName.text.isNullOrBlank() && !binding.registerCpf.text.isNullOrBlank() && (!binding.registerPhone.text!!.equals(
                    "("
                ) && !binding.registerPhone.text.isNullOrBlank()) && viewModel.validCpf.value!!
            }
        }
    }

    private fun isSecondScreenValidate(): Boolean {
        return !register_cep.text.isNullOrBlank() && !register_street.text.isNullOrBlank() && !register_number.text.isNullOrBlank() && !register_city.text.isNullOrBlank() && !register_state.text.isNullOrBlank()
    }

    private fun isThirdScreenValidate(): Boolean {
        if (binding.registerEmail.text.isNullOrBlank()) {
            return false
        }

        if (viewModel.validEmail.value == null) {
            viewModel.validateEmail(this)

            return false
        } else {
            return if (!register_password.text.isNullOrBlank() && !register_confirm_password.text.isNullOrBlank()) {
                if (register_password.text?.length!! < 6){
                    showError("A senha deve 6 ou mais caracteres.")
                    return false
                }

                if (register_password.text.toString() != register_confirm_password.text.toString()) {
                    showError(getString(R.string.passwords_not_matching))
                    false
                } else {
                    !register_email.text.isNullOrBlank()
                }
            } else {
                showError("Preencha os campos acima")
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

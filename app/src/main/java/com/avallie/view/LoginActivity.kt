package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avallie.R
import com.avallie.helpers.AppHelper.Companion.getSnackbar
import com.avallie.view.fragment.ProgressDialog
import kotlinx.android.synthetic.main.activity_login.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        back_button.setOnClickListener {
            finish()
        }

        btn_login.setOnClickListener {
            ProgressDialog(this, "Autenticando seus dados, por favor aguarde.").show()
        }

        create_account.setOnClickListener {
            Intent(this, RegisterActivity::class.java).run{
                startActivity(this)
            }
        }
    }

    private fun showErrorMessage(errorMessage: String) {
        getSnackbar(this, root_layout, errorMessage).show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

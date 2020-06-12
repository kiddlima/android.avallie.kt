package com.avallie.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.avallie.databinding.ActivityAccountBinding
import com.avallie.helpers.AuthHelper
import com.avallie.helpers.PaperHelper
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class AccountActivity : AppCompatActivity() {

    lateinit var viewModel: AccountViewModel

    lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        binding = ActivityAccountBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this

        viewModel.getCustomer(this)

        binding.viewModel = viewModel

        binding.logoutButton.setOnClickListener {
            PaperHelper.clearCustomer()

            AuthHelper.logout()

            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

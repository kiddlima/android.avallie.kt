package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.avallie.R
import com.avallie.databinding.ActivityAccountBinding
import com.avallie.helpers.AuthHelper
import com.avallie.helpers.PaperHelper
import com.avallie.view.newAddress.MyAddressActivity
import com.avallie.widgets.MenuItemComponent
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class AccountActivity : AppCompatActivity() {

    lateinit var viewModel: AccountViewModel

    lateinit var binding: ActivityAccountBinding

    lateinit var addressMenu: MenuItemComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        binding = ActivityAccountBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this

        addressMenu = MenuItemComponent(
            getString(R.string.multiple_address),
            getString(R.string.my_delivery_address),
            ContextCompat.getDrawable(this, R.drawable.ic_location)!!, View.OnClickListener {
                startActivity(Intent(this, MyAddressActivity::class.java))
            })

        viewModel.getCustomer(this)

        binding.viewModel = viewModel
        binding.activity = this

        binding.logoutButton.setOnClickListener {
            PaperHelper.clearCustomer()

            viewModel.deleteNotificationToken(this)

            AuthHelper.logout()

            startActivity(Intent(this, LoginActivity::class.java))

            finish()
        }

        setContentView(binding.root)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

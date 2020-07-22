package com.avallie.view.address

import android.content.Intent
import android.os.Bundle
import com.avallie.core.CustomActivity
import com.avallie.databinding.ActivityMyAddressBinding

class MyAddressActivity : CustomActivity() {

    lateinit var binding: ActivityMyAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAddressBinding.inflate(layoutInflater)

        binding.addressSearchView.setOnClickListener {
            startActivity(Intent(this, FindAddressActivity::class.java))
        }

        setContentView(binding.root)
    }

}
package com.avallie.view.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avallie.R
import kotlinx.android.synthetic.main.activity_register_success.*

class RegisterSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_success)

        btn_start.setOnClickListener {
            finish()
        }
    }
}

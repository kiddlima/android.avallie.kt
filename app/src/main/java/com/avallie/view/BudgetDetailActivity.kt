package com.avallie.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avallie.R
import kotlinx.android.synthetic.main.activity_budget_detail.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class BudgetDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_detail)

        close_detail.setOnClickListener {
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

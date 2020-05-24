package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.model.BudgetRequested
import com.avallie.view.adapter.BudgetProductsAdapter
import kotlinx.android.synthetic.main.activity_budget_detail.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class BudgetDetailActivity : AppCompatActivity() {

    private val productsAdapter: BudgetProductsAdapter by lazy {
        BudgetProductsAdapter(this, budgetDetail.products!!)
    }

    private lateinit var budgetDetail: BudgetRequested

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_detail)

        budgetDetail = intent.getSerializableExtra("budget_request") as BudgetRequested

        budget_name.text = budgetDetail.budgetName
        deadline_detail.text = budgetDetail.budgetDate
        address_detail.text = budgetDetail.address

        setAdapter()

        close_detail.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter() {
        budget_products_recycler.adapter = productsAdapter
        budget_products_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        productsAdapter.onProductSelectedProduct = {
            startActivity(Intent(this, BudgetProductDetailActivity::class.java).putExtra("selected-product", it))
        }
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

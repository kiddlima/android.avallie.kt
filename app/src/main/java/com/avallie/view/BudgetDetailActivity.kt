package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.model.BudgetRequested
import com.avallie.model.Product
import com.avallie.model.RequestedProduct
import com.avallie.view.adapter.BudgetProductsAdapter
import kotlinx.android.synthetic.main.activity_budget_detail.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class BudgetDetailActivity : AppCompatActivity() {

    private val productsAdapter: BudgetProductsAdapter by lazy {
        BudgetProductsAdapter(this, budgetDetail.products!!)
    }

    private var budgetDetail: BudgetRequested =
        BudgetRequested("Materias para banheiro", "12/04/1995", "Av Polar, 415",1, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_detail)

        mockProducts()
        setAdapter()

        close_detail.setOnClickListener {
            finish()
        }
    }

    private fun mockProducts() {
        budgetDetail.products = ArrayList()

        for (i in 0..4) {
            val product = Product(2, "Barra de aço", "AÇO", "Espessura e comprimento", "un", false)
            val selectedProduct = RequestedProduct(2, "Sem correia", product, 10, ArrayList())

            budgetDetail.products?.add(selectedProduct)
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

package com.avallie.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.model.BudgetRequested
import com.avallie.model.Product
import com.avallie.model.SelectedProduct
import com.avallie.view.adapter.BudgetProductsAdapter
import kotlinx.android.synthetic.main.activity_budget_detail.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*

class BudgetDetailActivity : AppCompatActivity() {

    private val productsAdapter: BudgetProductsAdapter by lazy {

        BudgetProductsAdapter(this, budgetDetail?.products!!)
    }

    private var budgetDetail: BudgetRequested? = BudgetRequested("Materias para banheiro", Date(), 3, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_detail)

        mockProducts()
        setAdapter()

        close_detail.setOnClickListener {
            finish()
        }
    }

    private fun setInfos(){

    }

    private fun mockProducts() {
        budgetDetail?.products = ArrayList()

        for (i in 0..4) {
            val product = Product("2", "Barra de aço", "AÇO", "Espessura e comprimento", "un", false)
            val selectedProduct = SelectedProduct(2, "Sem correia", product, 10)

            budgetDetail?.products?.add(selectedProduct)
        }
    }

    private fun setAdapter() {
        budget_products_recycler.adapter = productsAdapter
        budget_products_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}

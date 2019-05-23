package com.avallie.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avallie.R
import com.avallie.model.Budget
import com.avallie.model.SelectedProduct

class BudgetProductDetailActivity : AppCompatActivity() {

    val SELECTED_PRODUCT = "selected-product"

    private val selectedProduct: SelectedProduct by lazy {
        intent.getSerializableExtra(SELECTED_PRODUCT) as SelectedProduct
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_product_detail)



        mockBudgets()
    }

    private fun mockBudgets() {
        for (i in 0..4) {
              selectedProduct.budgets.add(Budget("Balarotti",
                    "Em até 12x no cartão sem juros",
                    "Em até 10 dias úteis após pagamento",
                    "Pronta entrega",
                    2599.0,
                    2399.0,
                    0.5,
                    75.0))
        }
    }
}

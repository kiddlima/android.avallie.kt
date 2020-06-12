package com.avallie.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avallie.databinding.ActivityBudgetProductDetailBinding
import com.avallie.model.Budget
import com.avallie.model.ScreenState
import com.avallie.model.RequestedProduct
import com.avallie.view.adapter.BudgetsAdapter
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class BudgetProductDetailActivity : AppCompatActivity() {

    private val SELECTED_PRODUCT = "selected-product"

    private lateinit var recyclerView: RecyclerView

    var screenState: ScreenState = ScreenState.Loading

    lateinit var requestedProduct: RequestedProduct

    val adapter: BudgetsAdapter by lazy {
        BudgetsAdapter(this, requestedProduct.budgets)
    }

    private lateinit var binding: ActivityBudgetProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetProductDetailBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this

        requestedProduct = intent.getSerializableExtra(SELECTED_PRODUCT) as RequestedProduct

        mockBudgets()
        binding.model = this

        setContentView(binding.root)

        recyclerView = binding.budgetsRecycler

        setAdapter()
    }

    private fun setAdapter() {
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun mockBudgets() {
        for (i in 0..4) {
            requestedProduct.budgets.add(
                Budget(
                    "Balarotti",
                    "Em até 12x no cartão sem juros",
                    "Em até 10 dias úteis após pagamento",
                    "Pronta entrega",
                    2599.0,
                    2399.0,
                    0.5,
                    75.0
                )
            )
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun onBackPressed(view: View) {
        super.onBackPressed()
    }
}

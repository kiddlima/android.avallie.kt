package com.avallie.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avallie.databinding.ActivityBudgetProductDetailBinding
import com.avallie.model.Budget
import com.avallie.model.ScreenState
import com.avallie.model.RequestedProduct
import com.avallie.view.adapter.BudgetsAdapter
import com.avallie.view.filter.FilterViewModel
import com.avallie.view.register.BudgetProductDetailViewModel
import kotlinx.android.synthetic.main.activity_budget_product_detail.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class BudgetProductDetailActivity : AppCompatActivity() {

    private val SELECTED_PRODUCT = "selected-product"

    private lateinit var recyclerView: RecyclerView

    lateinit var viewModel: BudgetProductDetailViewModel

    val adapter: BudgetsAdapter by lazy {
        BudgetsAdapter(this, viewModel.requestedProduct.value!!.budgets)
    }

    private lateinit var binding: ActivityBudgetProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetProductDetailBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(BudgetProductDetailViewModel::class.java)

        viewModel.requestedProduct.value = intent.getSerializableExtra(SELECTED_PRODUCT) as RequestedProduct

        viewModel.getSelectedProductResponses(this)

        viewModel.screenState.observe(this, Observer {
            if (it == ScreenState.Success) {
                setAdapter()
            }
        })

        binding.model = this
        binding.viewModel = viewModel

        setContentView(binding.root)

        binding.productDetailHeader.text = viewModel.requestedProduct.value!!.product.name.toLowerCase().capitalize()

        recyclerView = binding.budgetsRecycler
    }

    private fun setAdapter() {
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun onBackPressed(view: View) {
        super.onBackPressed()
    }
}

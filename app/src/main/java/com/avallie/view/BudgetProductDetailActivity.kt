package com.avallie.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avallie.databinding.ActivityBudgetProductDetailBinding
import com.avallie.helpers.SpecHelper
import com.avallie.model.RequestedProduct
import com.avallie.model.ScreenState
import com.avallie.view.adapter.BudgetsAdapter
import com.avallie.view.register.BudgetProductDetailViewModel
import com.avallie.widgets.NoDataContainer
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

        viewModel.requestedProduct.value =
            intent.getSerializableExtra(SELECTED_PRODUCT) as RequestedProduct

        viewModel.getSelectedProductResponses(this)

        viewModel.screenState.observe(this, Observer {
            if (it == ScreenState.Success) {
                setAdapter()
            }
        })

        binding.model = this
        binding.viewModel = viewModel

        viewModel.requestedProduct.value?.specifications =
            viewModel.requestedProduct.value?.specifications?.replace(":", ": ")!!


        viewModel.requestedProduct.value?.run {
            binding.productDetailDescription.text = formatProductDescription(this)

        }

        binding.noDataContainer = NoDataContainer(
            "Nenhum orçamento disponível ainda!",
            "Enviaremos uma notificação assim que algum fornecedor responder"
        )

        setContentView(binding.root)

        binding.productDetailHeader.text =
            viewModel.requestedProduct.value!!.product.name.toLowerCase().capitalize()

        recyclerView = binding.budgetsRecycler
    }

    private fun formatProductDescription(requestedProduct: RequestedProduct): String {
        val specs = SpecHelper.createSpecs(viewModel.requestedProduct.value?.specifications!!)

        val specsString = SpecHelper.formattedSpecsString(specs)

        return "${if (requestedProduct.brand != null) "Marca de preferência: ${requestedProduct.brand}\n" else ""}${specsString}"
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

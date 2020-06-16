package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.databinding.ActivityRequestedBudgetsBinding
import com.avallie.model.BudgetRequested
import com.avallie.model.ScreenState
import com.avallie.view.adapter.BudgetRequestsAdapter
import com.avallie.view.fragment.BudgetRequestsViewModel
import com.avallie.widgets.NoDataContainer
import kotlinx.android.synthetic.main.budget_requests_fragment.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class RequestedBudgetsActivity : AppCompatActivity() {

    private lateinit var budgetRequestedAdapter: BudgetRequestsAdapter

    lateinit var viewModel: BudgetRequestsViewModel

    lateinit var binding: ActivityRequestedBudgetsBinding

    private var btnResponseAction: Button? = null
    private var responseSubTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestedBudgetsBinding.inflate(layoutInflater)

        viewModel = ViewModelProviders.of(this).get(BudgetRequestsViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.activity = this

        btnResponseAction = binding.failLayout.findViewById(R.id.btn_response_action)
        responseSubTitle = binding.failLayout.findViewById(R.id.response_subtitle)

        binding.noData = NoDataContainer(
            "Você não realizou nenhum orçamento ainda",
            "Selecione os produtos que deseja e faça o seu primeiro orçamento agora mesmo!"
        )

        binding.failLayout.visibility = View.GONE

        viewModel.screenState.value = ScreenState.Loading

        viewModel.screenState.observe(this, Observer {
            when (it) {
                ScreenState.Success -> setBudgetsView()
                ScreenState.Loading -> getBudgetsRequested()
                ScreenState.Fail -> setFailView()
            }
        })

        setContentView(binding.root)
    }

    private fun goToDetail(budgetRequested: BudgetRequested, selectedProductId: Long?) {
        if (selectedProductId != -1L) {
            startActivity(
                Intent(this, BudgetProductDetailActivity::class.java)
                    .putExtra(
                        "selected-product",
                        budgetRequested.products!!.single { selectedProduct -> selectedProduct.id == selectedProductId }
                    )
            )
        } else {
            val intent = Intent(this, BudgetDetailActivity::class.java)
            intent.putExtra("budget_request", budgetRequested)
            startActivity(intent)
        }
    }

    private fun setBudgetsView() {
        intent.getLongExtra("budget_id", -1L).run {
            if (this != -1L) {
                goToDetail(
                    viewModel.budgetsRequested.value?.single { budgetRequested -> budgetRequested.id == this }!!,
                    intent.getLongExtra("selected_product_id", -1L)
                )
            }
        }

        setAdapter()
    }

    private fun setAdapter() {
        budgetRequestedAdapter =
            BudgetRequestsAdapter(this, viewModel.budgetsRequested.value!!)
        budgetRequestedAdapter.onBudgetSelected = { budgetRequested ->
            goToDetail(budgetRequested, null)
        }

        requests_recycler.adapter = budgetRequestedAdapter
        requests_recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setFailView() {
        responseSubTitle?.text = "Erro ao carregar solicitações"
        btnResponseAction?.setOnClickListener {
            viewModel.screenState.value = ScreenState.Loading
        }
    }

    private fun getBudgetsRequested() {
        viewModel.getRequestedBudgets(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
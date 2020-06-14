package com.avallie.view.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.model.BudgetRequested
import com.avallie.model.ScreenState
import com.avallie.view.BudgetDetailActivity
import com.avallie.view.MainActivity
import com.avallie.view.adapter.BudgetRequestsAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.budget_requests_fragment.*

class BudgetRequestsFragment : BottomSheetDialogFragment() {

    private lateinit var budgetRequestedAdapter: BudgetRequestsAdapter

    lateinit var viewModel: BudgetRequestsViewModel

    private var responseContainer: View? = null
    private var btnResponseAction: Button? = null
    private var responseSubTitle: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.budget_requests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        responseContainer = view.findViewById<ConstraintLayout>(R.id.response_container)
        btnResponseAction = view.findViewById(R.id.btn_response_action)
        responseSubTitle = view.findViewById(R.id.response_subtitle)



        viewModel = ViewModelProviders.of(this).get(BudgetRequestsViewModel::class.java)

        viewModel.screenState.value = ScreenState.Loading

        viewModel.screenState.observe(this, Observer {
            isCancelable = it != ScreenState.Loading

            when (it) {
                ScreenState.Success -> setBudgetsView()
                ScreenState.Loading -> setLoadingView()
                ScreenState.Fail -> setFailView()
                ScreenState.NoData -> setNoDataView()
            }
        })
    }

    private fun setBudgetsView() {
        progress_container.visibility = View.GONE

        arguments?.getLong("budget_id")?.run {
            if (this != -1L) {
                goToDetail(viewModel.budgetsRequested.value?.single { budgetRequested -> budgetRequested.id == this }!!)
            }
        }

        setAdapter()
    }

    private fun setLoadingView() {
        responseContainer?.visibility = View.GONE
        progress_container.visibility = View.VISIBLE
        getBudgetsRequested()
    }

    private fun setFailView() {
        progress_container.visibility = View.GONE
        responseContainer?.visibility = View.VISIBLE
        responseSubTitle?.text = "Erro ao carregar solicitações"
        btnResponseAction?.setOnClickListener {
            viewModel.screenState.value = ScreenState.Loading

        }
    }

    private fun setNoDataView() {
        //TODO MOCK FIRST
    }

    private fun getBudgetsRequested() {
        viewModel.getRequestedBudgets(context!!)
    }

    private fun goToDetail(budgetRequested: BudgetRequested) {
        activity?.let {
            val intent = Intent(it, BudgetDetailActivity::class.java)
            intent.putExtra("budget_request", budgetRequested)
            it.startActivity(intent)
        }
    }

    private fun setAdapter() {
        budgetRequestedAdapter =
            BudgetRequestsAdapter(context!!, viewModel.budgetsRequested.value!!)
        budgetRequestedAdapter.onBudgetSelected = { budgetRequested ->
            goToDetail(budgetRequested)
        }

        requests_recycler.adapter = budgetRequestedAdapter
        requests_recycler.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        (activity as MainActivity).updateSelectedItem()
    }

}
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.model.BudgetRequested
import com.avallie.view.BudgetDetailActivity
import com.avallie.view.MainActivity
import com.avallie.view.adapter.BudgetRequestsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.budget_requests_fragment.*
import java.util.*

class BudgetRequestsFragment : BottomSheetDialogFragment() {

    private val budgetsRequested: ArrayList<BudgetRequested> = ArrayList()

    private lateinit var budgetRequestedAdapter: BudgetRequestsAdapter

    private var responseContainer: View? = null
    private var btnResponseAction: Button? = null
    private var responseSubTitle: TextView? = null

    enum class RequestsScreen {
        BUDGETS,
        LOADING,
        FAIL,
        NODATA
    }

    private var requestsScreen = RequestsScreen.LOADING

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.budget_requests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        responseContainer = view.findViewById<ConstraintLayout>(R.id.response_container)
        btnResponseAction = view.findViewById(R.id.btn_response_action)
        responseSubTitle = view.findViewById(R.id.response_subtitle)

        reloadBudgetsView()
    }

    private fun reloadBudgetsView() {
        isCancelable = requestsScreen != RequestsScreen.LOADING

        when (requestsScreen) {
            RequestsScreen.BUDGETS -> setBudgetsView()
            RequestsScreen.LOADING -> setLoadingView()
            RequestsScreen.FAIL -> setFailView()
            RequestsScreen.NODATA -> setNoDataView()
        }
    }

    private fun setBudgetsView() {
        progress_container.visibility = View.GONE

        mockRequests()
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
            requestsScreen = RequestsScreen.LOADING
            reloadBudgetsView()
        }
    }

    private fun setNoDataView() {
        //TODO MOCK FIRST
    }

    private fun getBudgetsRequested() {
        //TODO HTTP REQUEST

        Handler().postDelayed({
            requestsScreen = RequestsScreen.BUDGETS
            reloadBudgetsView()
        }, 500)
    }

    private fun setAdapter() {
        budgetRequestedAdapter = BudgetRequestsAdapter(context!!, budgetsRequested)
        budgetRequestedAdapter.onBudgetSelected = {
            activity?.let {
                val intent = Intent(it, BudgetDetailActivity::class.java)
                it.startActivity(intent)
            }
        }

        requests_recycler.adapter = budgetRequestedAdapter
        requests_recycler.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }

    private fun mockRequests() {
        for (i in 0..10) {
            val budgetRequested = BudgetRequested("Orçamento", Date(), 7, null)

            budgetsRequested.add(budgetRequested)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        (activity as MainActivity).updateSelectedItem()
    }

}
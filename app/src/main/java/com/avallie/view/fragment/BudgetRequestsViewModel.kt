package com.avallie.view.fragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.model.BudgetRequested
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class BudgetRequestsViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()

    val screenState = MutableLiveData<ScreenState>()

    val budgetsRequested = MutableLiveData<MutableList<BudgetRequested>>()

    fun getRequestedBudgets(context: Context) {
        HttpService(context).getBudgetsRequested(object : ConnectionListener<MutableList<BudgetRequested>> {
            override fun onSuccess(response: MutableList<BudgetRequested>) {
                budgetsRequested.value = response
                screenState.value = ScreenState.Success
            }

            override fun onFail(error: String?) {
                screenState.value = ScreenState.Fail
                errorMessage.value = error
            }

            override fun noInternet() {
                screenState.value = ScreenState.Fail
                errorMessage.value = context.getString(R.string.server_error)
            }

        })
    }

}
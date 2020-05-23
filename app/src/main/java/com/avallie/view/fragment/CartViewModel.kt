package com.avallie.view.fragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.model.request.BudgetRequest
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class CartViewModel: ViewModel() {

    val errorMessage = MutableLiveData<String>()

    val screenState = MutableLiveData<ScreenState>()

    var cartScreenState = MutableLiveData<CartFragment.CartScreen>()

    fun requestBudget (budgetRequest: BudgetRequest, context: Context) {
        HttpService(context).requestBudget(budgetRequest, object: ConnectionListener<Any> {
            override fun onSuccess(response: Any) {
                cartScreenState.value = CartFragment.CartScreen.SUCCESS
            }

            override fun onFail(error: String?) {
                errorMessage.value = error
                cartScreenState.value = CartFragment.CartScreen.ERROR
            }

            override fun noInternet() {
                cartScreenState.value = CartFragment.CartScreen.ERROR
                errorMessage.value = context.getString(R.string.server_error)
            }
        })
    }

}
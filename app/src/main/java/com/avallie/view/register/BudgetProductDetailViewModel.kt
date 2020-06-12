package com.avallie.view.register

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.model.Budget
import com.avallie.model.RequestedProduct
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class BudgetProductDetailViewModel : ViewModel() {

    val requestedProduct = MutableLiveData<RequestedProduct>()

    val screenState = MutableLiveData<ScreenState>()

    val errorMessage = MutableLiveData<String>()

    fun getSelectedProductResponses(context: Context) {
        screenState.value = ScreenState.Loading

        HttpService(context).getSelectedProductResponses(
                requestedProduct.value!!.id.toString(),
                object : ConnectionListener<MutableList<Budget>> {
                    override fun onSuccess(response: MutableList<Budget>) {
                        requestedProduct.value?.run {
                            this.budgets = response

                            screenState.value = ScreenState.Success
                        }
                    }

                    override fun onFail(error: String?) {
                        errorMessage.value = error

                        screenState.value = ScreenState.Fail
                    }

                    override fun noInternet() {
                        errorMessage.value = context.getString(R.string.server_error)

                        screenState.value = ScreenState.Fail
                    }
                })
    }
}
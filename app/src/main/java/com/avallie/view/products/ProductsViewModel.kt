package com.avallie.view.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.R
import com.avallie.model.Product
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class ProductsViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val products = MutableLiveData<List<Product>>()
    val categories = MutableLiveData<ArrayList<String>>()

    val screenState = MutableLiveData<ScreenState>()

    init {
        categories.value?.clear()
        screenState.value = ScreenState.Loading
    }

    fun loadProducts(context: Context) {
        screenState.value = ScreenState.Loading

        HttpService(context).getProducts(
            categories.value!!,
            object : ConnectionListener<List<Product>> {
                override fun onSuccess(response: List<Product>) {
                    products.value = emptyList()
                    products.value = response

                    if (products.value == null) {
                        screenState.value = ScreenState.NoData
                    } else {
                        screenState.value = ScreenState.Success
                    }
                }

                override fun onFail(error: String?) {
                    errorMessage.value = error

                    screenState.value = ScreenState.Fail
                }

                override fun noInternet() {
                    errorMessage.value = context.resources.getString(R.string.no_connection)

                    screenState.value = ScreenState.Fail
                }
            })
    }


}
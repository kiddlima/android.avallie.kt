package com.avallie.view.products

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.avallie.R
import com.avallie.model.Product
import com.avallie.model.ScreenState
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class ProductsViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val products = MutableLiveData<List<Product>>()
    val categories = MutableLiveData<ArrayList<String>>()
    val productSearchName = MutableLiveData<String>()

    val screenState = MutableLiveData<ScreenState>()

    var liveDataSource: MutableLiveData<PageKeyedDataSource<Int, Product>>? = null

    var itemPagedList: LiveData<PagedList<Product>>? = null

    init {
        categories.value?.clear()
        productSearchName.value = ""
        screenState.value = ScreenState.Loading
    }

    fun loadProducts(context: Context, filter: ProductsQuery) {
        screenState.value = ScreenState.Loading
        filter.categories = ArrayList(filter.categories)

        val productsSourceFactory = ProductsSourceFactory(
            HttpService(context),
            filter,
            object : ConnectionListener<ProductsContainerResponse> {
                override fun onSuccess(response: ProductsContainerResponse) {
                    if (response.totalElements == 0) {
                        screenState.value = ScreenState.NoData
                    } else {
                        screenState.value = ScreenState.Success
                    }
                }

                override fun onFail(error: String?) {
                    errorMessage.value = "Ocorreu um erro ao consultar a lista de produtos"

                    screenState.value = ScreenState.Fail
                }

                override fun noInternet() {
                    errorMessage.value = context.resources.getString(R.string.server_error)

                    screenState.value = ScreenState.Fail
                }
            })

        liveDataSource = productsSourceFactory.itemLiveDataSource

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(filter.size)
            .setInitialLoadSizeHint(filter.size * 2)
            .build()

        itemPagedList = LivePagedListBuilder(productsSourceFactory, pagedListConfig).build()
    }


}
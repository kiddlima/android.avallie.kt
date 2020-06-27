package com.avallie.view.products

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.avallie.model.Product
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class ProductsSourceFactory(
    val httpService: HttpService,
    val filter: ProductsQuery,
    val connectionListener: ConnectionListener<ProductsContainerResponse>
) : DataSource.Factory<Int, Product>() {

    val itemLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Product>>()

    override fun create(): DataSource<Int, Product> {
        val dataSource = ProductsDataSource(httpService, filter, connectionListener)

        itemLiveDataSource.postValue(dataSource)

        return dataSource
    }
}

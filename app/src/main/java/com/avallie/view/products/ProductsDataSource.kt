package com.avallie.view.products

import androidx.paging.PageKeyedDataSource
import com.avallie.model.Product
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService

class ProductsDataSource(
    private val service: HttpService,
    private val filter: ProductsQuery,
    private val connectionListener: ConnectionListener<ProductsContainerResponse>
) : PageKeyedDataSource<Int, Product>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Product>
    ) {
        service.getProducts(
            filter,
            object : ConnectionListener<ProductsContainerResponse> {
                override fun onSuccess(response: ProductsContainerResponse) {
                    connectionListener.onSuccess(response)
                    callback.onResult(response.content, null, response.pageable.pageNumber + 1)
                }

                override fun onFail(error: String?) {
                    connectionListener.onFail(error)
                }

                override fun noInternet() {
                    connectionListener.onFail(null)
                }
            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {
        filter.page = params.key

        service.getProducts(
            filter,
            object : ConnectionListener<ProductsContainerResponse> {
                override fun onSuccess(response: ProductsContainerResponse) {
                    if (response.last) {
                        callback.onResult(response.content, null)
                    } else {
                        callback.onResult(response.content, response.pageable.pageNumber + 1)
                    }

                    connectionListener.onSuccess(response)
                }

                override fun onFail(error: String?) {
                    connectionListener.onFail(error)
                }

                override fun noInternet() {
                    connectionListener.onFail(null)
                }
            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {
    }
}

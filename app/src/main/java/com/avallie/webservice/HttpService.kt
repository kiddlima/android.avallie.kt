package com.avallie.webservice

import android.content.Context
import com.avallie.BuildConfig
import com.avallie.model.ConstructionPhase
import com.avallie.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HttpService(private val context: Context) {

    private val requestClient by lazy {
        RequestClient(context, BuildConfig.BASE_URL).retrofit.create(Services::class.java)
    }

    private inline fun <reified T> cast(from: Any?): T? = from as? T

    fun getAllPhases(connectionListener: ConnectionListener<List<ConstructionPhase>>) {
        requestClient.getAllPhases().enqueue(object : Callback<List<ConstructionPhase>> {
            override fun onResponse(call: Call<List<ConstructionPhase>>, response: Response<List<ConstructionPhase>>) {

                val responseBody = cast<ArrayList<ConstructionPhase>>(response.body())

                if (responseBody != null){

                    for(phase in responseBody){
                        phase.parseCategories()
                    }

                    connectionListener.onSuccess(responseBody)
                } else {
                    connectionListener.onFail(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<ConstructionPhase>>, t: Throwable) {
                connectionListener.noInternet()
            }
        })
    }


    fun getProducts(categories: ArrayList<String>, name: String?, connectionListener: ConnectionListener<ArrayList<Product>>) {
        requestClient.getProducts(categories, name).enqueue(object : Callback<ArrayList<Product>> {
            override fun onResponse(call: Call<ArrayList<Product>>, response: Response<ArrayList<Product>>) {

                val responseBody = cast<ArrayList<Product>>(response.body())

                if (responseBody != null){
                    connectionListener.onSuccess(responseBody)
                } else {
                    connectionListener.onFail(response.errorBody().toString())
                }

            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                connectionListener.noInternet()
            }
        })

    }

}

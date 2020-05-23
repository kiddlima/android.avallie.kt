package com.avallie.webservice

import android.content.Context
import com.avallie.BuildConfig
import com.avallie.model.ApiResponse
import com.avallie.model.ConstructionPhase
import com.avallie.model.Customer
import com.avallie.model.Product
import com.avallie.model.request.BudgetRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HttpService(private val context: Context) {

    private val requestClient by lazy {
        RequestClient(context, BuildConfig.BASE_URL).retrofit.create(Services::class.java)
    }

    private lateinit var authRequestClient: Services

    private inline fun <reified T> cast(from: Any?): T? = from as? T

    fun getAllPhases(connectionListener: ConnectionListener<List<ConstructionPhase>>) {
        requestClient.getAllPhases().enqueue(object : Callback<ApiResponse<List<ConstructionPhase>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<ConstructionPhase>>>,
                response: Response<ApiResponse<List<ConstructionPhase>>>
            ) {

                val responseBody = cast<ApiResponse<ArrayList<ConstructionPhase>>>(response.body())

                if (responseBody != null) {

                    for (phase in responseBody.data) {
                        phase.parseCategories()
                    }

                    connectionListener.onSuccess(responseBody.data)
                } else {
                    connectionListener.onFail(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<ConstructionPhase>>>, t: Throwable) {
                connectionListener.noInternet()
            }
        })
    }

    fun getProducts(categories: List<String>, connectionListener: ConnectionListener<List<Product>>) {

        var categoriesPath = ""

        if (categories.isNotEmpty()) {
            categoriesPath = categories[0]
            for (i in 1 until categories.size) {
                categoriesPath = "$categoriesPath, ${categories[i]}"
            }
        }

        requestClient.getProducts(categoriesPath).enqueue(object : Callback<ApiResponse<ArrayList<Product>>> {
            override fun onResponse(
                call: Call<ApiResponse<ArrayList<Product>>>,
                response: Response<ApiResponse<ArrayList<Product>>>
            ) {
                val responseBody = cast<ApiResponse<ArrayList<Product>>>(response.body())

                if (responseBody != null) {
                    connectionListener.onSuccess(responseBody.data)
                } else {
                    connectionListener.onFail(response.message())
                }

            }

            override fun onFailure(call: Call<ApiResponse<ArrayList<Product>>>, t: Throwable) {
                connectionListener.noInternet()
            }
        })
    }

    fun registerCustomer(customer: Customer, connectionListener: ConnectionListener<Any>) {
        requestClient.registerCustomer(customer).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                connectionListener.noInternet()
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.body() != null && response.isSuccessful) {
                    connectionListener.onSuccess(response.body()!!)
                } else {
                    connectionListener.onFail(response.errorBody().toString())
                }
            }
        })
    }

    fun requestBudget(budgetRequest: BudgetRequest, connectionListener: ConnectionListener<Any>) {
        //TODO Refactor
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
            authRequestClient =
                RequestClient(context, BuildConfig.BASE_URL).retrofit.create(Services::class.java)

            authRequestClient.requestBudget("Bearer " + it.result?.token!!, budgetRequest).enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                    connectionListener.noInternet()
                }

                override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                    if (response.body() != null && response.isSuccessful) {
                        connectionListener.onSuccess(response.body()!!)
                    } else {
                        connectionListener.onFail(response.errorBody().toString())
                    }
                }
            })
        }
    }

}

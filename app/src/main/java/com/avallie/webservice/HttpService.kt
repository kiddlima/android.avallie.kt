package com.avallie.webservice

import android.content.Context
import com.avallie.BuildConfig
import com.avallie.model.*
import com.avallie.model.request.BudgetRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HttpService(private val context: Context) {

    private val requestClient by lazy {
        RequestClient(context, BuildConfig.BASE_URL, false).retrofit.create(Services::class.java)
    }

    private val authRequestClient by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        RequestClient(context, BuildConfig.BASE_URL, true).retrofit.create(Services::class.java)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

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

    fun getProducts(categories: MutableList<String>, connectionListener: ConnectionListener<List<Product>>) {

        requestClient.getProducts(categories).enqueue(object : Callback<ApiResponse<ArrayList<Product>>> {
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
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener {

            authRequestClient.requestBudget("Bearer " + it.result?.token!!, budgetRequest)
                .enqueue(object : Callback<ApiResponse<Any>> {
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

    fun getBudgetsRequested(connectionListener: ConnectionListener<MutableList<BudgetRequested>>) {
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
            authRequestClient.getBudgetsRequested("Bearer " + it.result?.token!!).enqueue(object : Callback<ApiResponse<MutableList<BudgetRequested>>> {
                override fun onFailure(call: Call<ApiResponse<MutableList<BudgetRequested>>>, t: Throwable) {
                    connectionListener.noInternet()
                }

                override fun onResponse(
                    call: Call<ApiResponse<MutableList<BudgetRequested>>>,
                    response: Response<ApiResponse<MutableList<BudgetRequested>>>
                ) {
                    if (response.body() != null && response.isSuccessful) {
                        connectionListener.onSuccess(response.body()!!.data)
                    } else {
                        connectionListener.onFail(response.errorBody().toString())
                    }
                }

            })
        }
    }

}

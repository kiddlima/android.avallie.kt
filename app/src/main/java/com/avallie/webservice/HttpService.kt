package com.avallie.webservice

import android.content.Context
import com.avallie.BuildConfig
import com.avallie.helpers.PaperHelper
import com.avallie.model.*
import com.avallie.model.request.BudgetRequest
import com.avallie.model.request.NotificationToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HttpService(private val context: Context) {

    private val requestClient by lazy {
        RequestClient(context, BuildConfig.BASE_URL, false).retrofit.create(Services::class.java)
    }


    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private inline fun <reified T> cast(from: Any?): T? = from as? T

    fun getAllPhases(connectionListener: ConnectionListener<List<ConstructionPhase>>) {
        requestClient.getAllPhases()
            .enqueue(object : Callback<ApiResponse<List<ConstructionPhase>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<ConstructionPhase>>>,
                    response: Response<ApiResponse<List<ConstructionPhase>>>
                ) {

                    val responseBody =
                        cast<ApiResponse<ArrayList<ConstructionPhase>>>(response.body())

                    if (responseBody != null) {

                        for (phase in responseBody.data) {
                            phase.parseCategories()
                        }

                        connectionListener.onSuccess(responseBody.data)
                    } else {
                        connectionListener.onFail(response.message())
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<List<ConstructionPhase>>>,
                    t: Throwable
                ) {
                    connectionListener.noInternet()
                }
            })
    }

    fun getProducts(
        categories: MutableList<String>,
        connectionListener: ConnectionListener<List<Product>>
    ) {
        if (categories.size == 1) {
            categories.add("")
        }

        requestClient.getProducts(categories)
            .enqueue(object : Callback<ApiResponse<ArrayList<Product>>> {
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

    fun requestBudget(
        budgetRequest: BudgetRequest,
        connectionListener: ConnectionListener<BudgetRequested>
    ) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener {

            requestClient.requestBudget("Bearer " + it.token, budgetRequest)
                .enqueue(object : Callback<ApiResponse<BudgetRequested>> {
                    override fun onFailure(call: Call<ApiResponse<BudgetRequested>>, t: Throwable) {
                        connectionListener.noInternet()
                    }

                    override fun onResponse(
                        call: Call<ApiResponse<BudgetRequested>>,
                        response: Response<ApiResponse<BudgetRequested>>
                    ) {
                        responseHandler(response, connectionListener)
                    }
                })
        }?.addOnFailureListener {
            connectionListener.onFail("Falha ao autenticar")
        }
    }

    fun getBudgetsRequested(connectionListener: ConnectionListener<MutableList<BudgetRequested>>) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener {
            requestClient.getBudgetsRequested("Bearer " + it.token)
                .enqueue(object : Callback<ApiResponse<MutableList<BudgetRequested>>> {
                    override fun onFailure(
                        call: Call<ApiResponse<MutableList<BudgetRequested>>>,
                        t: Throwable
                    ) {
                        connectionListener.noInternet()
                    }

                    override fun onResponse(
                        call: Call<ApiResponse<MutableList<BudgetRequested>>>,
                        response: Response<ApiResponse<MutableList<BudgetRequested>>>
                    ) {
                        responseHandler(response, connectionListener)
                    }

                })
        }?.addOnFailureListener {
            connectionListener.onFail("Falha ao autenticar")
        }
    }

    fun getCustomer(connectionListener: ConnectionListener<Customer>) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener {
            requestClient.getCustomer("Bearer ${it.token}")
                .enqueue(object : Callback<ApiResponse<Customer>> {
                    override fun onFailure(call: Call<ApiResponse<Customer>>, t: Throwable) {
                        connectionListener.noInternet()
                    }

                    override fun onResponse(
                        call: Call<ApiResponse<Customer>>,
                        response: Response<ApiResponse<Customer>>
                    ) {
                        if (response.body() != null && response.isSuccessful) {
                            PaperHelper.saveCustomer(response.body()!!.data)

                            connectionListener.onSuccess(response.body()!!.data)
                        } else {
                            connectionListener.onFail(response.errorBody().toString())
                        }
                    }

                })
        }?.addOnFailureListener {
            connectionListener.onFail("Falha ao autenticar")
        }
    }

    fun setNotificationToken(connectionListener: ConnectionListener<Any>?) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener { authTokenResult ->
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceId ->
                requestClient.setNotificationToken(
                    "Bearer " + authTokenResult.token,
                    NotificationToken(instanceId.token)
                ).enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        connectionListener?.noInternet()
                    }

                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        responseHandler(response, connectionListener)
                    }
                })
            }
        }?.addOnFailureListener {
            connectionListener?.onFail("Falha ao autenticar")
        }
    }

    fun getSelectedProductResponses(
        selectedProductId: String,
        connectionListener: ConnectionListener<MutableList<Budget>>
    ) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener { authTokenResult ->
            requestClient.getSelectedProductReponses(
                "Bearer " + authTokenResult.token,
                selectedProductId
            )
                .enqueue(object : Callback<ApiResponse<MutableList<Budget>>> {
                    override fun onFailure(
                        call: Call<ApiResponse<MutableList<Budget>>>,
                        t: Throwable
                    ) {
                        connectionListener.noInternet()
                    }

                    override fun onResponse(
                        call: Call<ApiResponse<MutableList<Budget>>>,
                        response: Response<ApiResponse<MutableList<Budget>>>
                    ) {
                        responseHandler(response, connectionListener)
                    }

                })
        }?.addOnFailureListener {
            connectionListener.onFail("Falha ao autenticar")
        }
    }

    fun validateCpf(cpf: String, connectionListener: ConnectionListener<Boolean>) {
        requestClient.validateCpf(cpf).enqueue(object : Callback<ApiResponse<Boolean>> {
            override fun onFailure(call: Call<ApiResponse<Boolean>>, t: Throwable) {
                connectionListener.noInternet()
            }

            override fun onResponse(
                call: Call<ApiResponse<Boolean>>,
                response: Response<ApiResponse<Boolean>>
            ) {
                responseHandler(response, connectionListener)
            }
        })
    }

    fun validateEmail(email: String, connectionListener: ConnectionListener<Boolean>) {
        requestClient.validateEmail(email).enqueue(object : Callback<ApiResponse<Boolean>> {
            override fun onFailure(call: Call<ApiResponse<Boolean>>, t: Throwable) {
                connectionListener.noInternet()
            }

            override fun onResponse(
                call: Call<ApiResponse<Boolean>>,
                response: Response<ApiResponse<Boolean>>
            ) {
                responseHandler(response, connectionListener)
            }
        })
    }


    fun <T> responseHandler(
        response: Response<ApiResponse<T>>,
        connectionListener: ConnectionListener<T>?
    ) {
        if (response.body() != null && response.isSuccessful) {
            connectionListener?.onSuccess(response.body()!!.data)
        } else {
            connectionListener?.onFail(response.errorBody().toString())
        }
    }

}

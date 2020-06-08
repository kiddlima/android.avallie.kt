package com.avallie.webservice

import com.avallie.model.*
import com.avallie.model.request.BudgetRequest
import retrofit2.Call
import retrofit2.http.*

interface Services {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/construction-phases")
    fun getAllPhases(): Call<ApiResponse<List<ConstructionPhase>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/products")
    fun getProducts(
        @Query("category") categories: MutableList<String>
    ): Call<ApiResponse<ArrayList<Product>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("v1/customers")
    fun registerCustomer(
        @Body customer: Customer
    ): Call<Any>

    @POST("v1/budget-requests")
    fun requestBudget(
        @Header("Authorization") token: String,
        @Body budgetRequest: BudgetRequest
    ): Call<ApiResponse<Any>>

    @GET("v1/budget-requests")
    fun getBudgetsRequested(
        @Header("Authorization") token: String
    ): Call<ApiResponse<MutableList<BudgetRequested>>>

    @GET("v1/customers/me")
    fun getCustomer(
        @Header("Authorization") token: String
    ): Call<ApiResponse<Customer>>


}
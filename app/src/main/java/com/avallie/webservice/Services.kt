package com.avallie.webservice

import com.avallie.model.*
import com.avallie.model.request.BudgetRequest
import retrofit2.Call
import retrofit2.http.*

interface Services {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/construction-phase")
    fun getAllPhases(): Call<ApiResponse<List<ConstructionPhase>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/products/{categories}")
    fun getProducts(
        @Path("categories") categories: String
    ): Call<ApiResponse<ArrayList<Product>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("v1/customer")
    fun registerCustomer(
        @Body customer: Customer
    ): Call<Any>

    @POST("v1/budget-request")
    fun requestBudget(
        @Header("Authorization") token: String,
        @Body budgetRequest: BudgetRequest

    ): Call<ApiResponse<Any>>

}
package com.avallie.webservice

import com.avallie.model.*
import com.avallie.model.request.BudgetRequest
import com.avallie.model.request.NotificationToken
import com.avallie.view.address.model.Address
import com.avallie.view.products.ProductsContainerResponse
import retrofit2.Call
import retrofit2.http.*

interface Services {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/construction-phases")
    fun getAllPhases(): Call<ApiResponse<List<ConstructionPhase>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/products")
    fun getProducts(
        @Query("category") categories: MutableList<String>,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiResponse<ProductsContainerResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("v1/customers")
    fun registerCustomer(
        @Body customer: Customer
    ): Call<Any>

    @POST("v1/budget-requests")
    fun requestBudget(
        @Header("Authorization") token: String,
        @Body budgetRequest: BudgetRequest
    ): Call<ApiResponse<BudgetRequested>>

    @GET("v1/budget-requests")
    fun getBudgetsRequested(
        @Header("Authorization") token: String
    ): Call<ApiResponse<MutableList<BudgetRequested>>>

    @GET("v1/customers/me")
    fun getCustomer(
        @Header("Authorization") token: String
    ): Call<ApiResponse<Customer>>

    @PUT("v1/customers/notification-info")
    fun setNotificationToken(
        @Header("Authorization") token: String,
        @Body body: NotificationToken
    ): Call<ApiResponse<Any>>

    @GET("v1/selected-products/{selected-product-id}/responses")
    fun getSelectedProductReponses(
        @Header("Authorization") token: String,
        @Path("selected-product-id") selectedProductId: String
    ): Call<ApiResponse<MutableList<Budget>>>

    @GET("v1/customers/cpf/{cpf}")
    fun validateCpf(
        @Path("cpf") cpf: String
    ): Call<ApiResponse<Boolean>>

    @GET("v1/customers/email/{email}")
    fun validateEmail(
        @Path("email") email: String
    ): Call<ApiResponse<Boolean>>

    @DELETE("v1/customers/notification-info")
    fun deleteTokenNotification(
        @Header("Authorization") token: String
    ): Call<ApiResponse<Boolean>>

    @POST("v1/customers/address")
    fun addAddress(
        @Header("Authorization") token: String,
        @Body body: Address
    ): Call<ApiResponse<Address>>
}
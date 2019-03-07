package com.avallie.webservice

import com.avallie.model.ConstructionPhase
import com.avallie.model.Product
import retrofit2.Call
import retrofit2.http.*

interface Services {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("getAllPhases")
    fun getAllPhases(): Call<List<ConstructionPhase>>

    @FormUrlEncoded
    @POST("getProducts")
    fun getProducts(
        @Field("categories") categories: ArrayList<String>,
        @Field("name") name: String?
    ): Call<ArrayList<Product>>


}
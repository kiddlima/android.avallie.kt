package com.avallie.webservice

import com.avallie.model.ConstructionPhase
import com.avallie.model.Product
import retrofit2.Call
import retrofit2.http.*

interface Services {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/constructionPhase")
    fun getAllPhases(): Call<List<ConstructionPhase>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("v1/products/{categories}")
    fun getProducts(
        @Path("categories") categories: String
    ): Call<ArrayList<Product>>


}
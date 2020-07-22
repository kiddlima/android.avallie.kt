package com.avallie.webservice

import com.avallie.model.OfficialAddress
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsServices {

    @GET("maps/api/place/nearbysearch/json")
    fun getAddress(
        @Query("address") address: String,
        @Query("region") region: String,
        @Query("key") key: String
    ): Call<JsonObject>

}
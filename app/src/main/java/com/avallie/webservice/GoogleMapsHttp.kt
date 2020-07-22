package com.avallie.webservice

import android.content.Context
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleMapsHttp(val context: Context) {

    private val requestClient by lazy {
        RequestClient(
            context,
            "https://maps.googleapis.com/",
            certificated = false,
            authenticated = false
        ).retrofit.create(GoogleMapsServices::class.java)
    }

    fun getAddress(address: String) {
        requestClient.getAddress(address, "br", "AIzaSyBOIN8-_2OF48P3ItWRhvPs9mHtUQI-pjY")
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    call
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    response
                }
            })
    }

}
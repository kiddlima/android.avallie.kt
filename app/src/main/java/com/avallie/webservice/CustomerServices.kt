package com.avallie.webservice

import com.avallie.model.OfficialAddress
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CustomerServices {

    @GET("ws/{cep}/json/")
    fun getCpfInfo(
        @Path("cep") cep: String
    ): Call<OfficialAddress>
}
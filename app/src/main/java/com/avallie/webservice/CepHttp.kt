package com.avallie.webservice

import android.content.Context
import com.avallie.R
import com.avallie.model.OfficialAddress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CepHttp(private val context: Context) {

    private val requestClient by lazy {
        RequestClient(
            context,
            "https://viacep.com.br/",
            certificated = false,
            authenticated = false
        ).retrofit.create(CustomerServices::class.java)
    }

    fun getCepInfo(cep: String, connectionListener: ConnectionListener<OfficialAddress>) {
        requestClient.getCpfInfo(cep).enqueue(object : Callback<OfficialAddress> {
            override fun onFailure(call: Call<OfficialAddress>, t: Throwable) {
                connectionListener.noInternet()
            }

            override fun onResponse(
                call: Call<OfficialAddress>,
                response: Response<OfficialAddress>
            ) {
                if (response.isSuccessful) {
                    connectionListener.onSuccess(response.body()!!)
                } else {
                    connectionListener.onFail(context.getString(R.string.cep_error))
                }
            }
        })
    }

}
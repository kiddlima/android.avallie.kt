package com.avallie.webservice

interface ConnectionListener<T> {
    fun onSuccess(response: T)
    fun onFail(error: String?)
    fun noInternet()
}
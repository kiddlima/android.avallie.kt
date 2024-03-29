package com.avallie.webservice

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.annimon.stream.ComparatorCompat.chain
import com.google.firebase.auth.GetTokenResult


class RequestClient(
    private val context: Context,
    private val baseURL: String,
    private val authenticated: Boolean
) {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    /** Retrofit with GSON converter **/
    val retrofit: Retrofit by lazy {
        getRetrofitObject()
    }


    private fun getRetrofitObject(): Retrofit{
        val builder = Retrofit.Builder()

        builder.baseUrl(baseURL)
        builder.addConverterFactory(getGsonConverterFactory())
        builder.client(getOkHttpClient())

        return builder.build()
    }

    private fun getGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        )
    }


    /** @return OkHttpClient with cache enabled **/
    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.addInterceptor(HeaderInterceptor())

        return builder.build()
    }

    /**
     * @return If there is Internet, get the cache that was stored  getNormalCacheTime() seconds ago.
     * If there is no Internet, get the cache that was stored [noInternetCacheTime] minutes ago.
     * **/
    private fun getRequestValueCache(): String {
        return "public, " + when {
            hasNetwork() -> {
                "max-age=" + getNormalCacheTime()

            }
            else -> {
                "only-if-cached, max-stale=" + getNoInternetCacheTime()
            }
        }
    }


    /** @return cache size in megabytes to retain objects **/
    private fun getCacheSize(): Long {
        return (1024 * 1024 * 5).toLong() // megabytes
    }


    /** @return time in minutes to retain objects in cache if there is no connection **/
    private fun getNormalCacheTime(): Long {
        return TimeUnit.MINUTES.toSeconds(2) // minutes
    }


    /** @return time in seconds to retain objects in cache if there is connection **/
    private fun getNoInternetCacheTime(): Long {
        return 20 // seconds
    }


    /** @return true if is connected to a network (doesn't mean necessarily that the device has internet connection) **/
    private fun hasNetwork(): Boolean {
        var isConnected = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) isConnected = true
        return isConnected
    }

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = chain.run {
            proceed(
                request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer")
                    .build()
            )
        }
    }
}
package com.avallie.webservice

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.avallie.R
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class RequestClient(
    private val context: Context,
    private val baseURL: String,
    private val certificated: Boolean,
    private val authenticated: Boolean
) {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    /** Retrofit with GSON converter **/
    val retrofit: Retrofit by lazy {
        getRetrofitObject()
    }

    private val okHttpClient: OkHttpClient.Builder = getOkHttpClient()

    private fun getRetrofitObject(): Retrofit {
        val builder = Retrofit.Builder()

        if (certificated) {
            initSSL(context)
        }

        builder.baseUrl(baseURL)
        builder.addConverterFactory(getGsonConverterFactory())
        builder.client(okHttpClient.build())

        return builder.build()
    }

    private fun getGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        )
    }

    /** @return OkHttpClient with cache enabled **/
    private fun getOkHttpClient(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()

        return builder
    }

    private fun initSSL(context: Context) {
        var sslContext: SSLContext? = null
        try {
            sslContext = createCertificate(context.resources.openRawResource(R.raw.server))
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        if (sslContext != null) {
            okHttpClient.sslSocketFactory(
                sslContext.socketFactory,
                systemDefaultTrustManager()
            )
        }
    }

    private fun createCertificate(trustedCertificateIS: InputStream): SSLContext? {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val ca: Certificate
        ca = try {
            cf.generateCertificate(trustedCertificateIS)
        } finally {
            trustedCertificateIS.close()
        }

        // creating a KeyStore containing our trusted CAs
        val keyStoreType: String = KeyStore.getDefaultType()
        val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        // creating a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // creating an SSLSocketFactory that uses our TrustManager
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, tmf.trustManagers, null)
        return sslContext
    }

    private fun systemDefaultTrustManager(): X509TrustManager? {
        return try {
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                )
            }
            trustManagers[0] as X509TrustManager
        } catch (e: GeneralSecurityException) {
            throw AssertionError() // The system has no TLS. Just give up.
        }
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
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
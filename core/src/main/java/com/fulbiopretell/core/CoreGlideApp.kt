package com.fulbiopretell.retoyape.core

import android.content.Context
import android.os.Build
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.fulbiopretell.retoyape.core.util.EnableTls
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.io.InputStream
import java.lang.Exception
import java.util.concurrent.TimeUnit

@GlideModule
class CoreGlideApp: AppGlideModule() {

    fun enableTls12OnPreLollipop(): OkHttpClient {

        val client = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .cache(null)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)

        if (Build.VERSION.SDK_INT !in 17..21) {
            return client.build()
        }

        try {
            EnableTls.startTrustManager()
            client.sslSocketFactory(EnableTls.startSocket())

            val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build()

            val specs = arrayListOf<ConnectionSpec>()
            specs.add(cs)
            specs.add(ConnectionSpec.COMPATIBLE_TLS)
            specs.add(ConnectionSpec.CLEARTEXT)

            client.connectionSpecs(specs)

        } catch (e: Exception) {
            Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", e)
        }

        return client.build()

    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        val factory = OkHttpUrlLoader.Factory(enableTls12OnPreLollipop())
        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)

    }

}

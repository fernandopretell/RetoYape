package com.fulbiopretell.retoyape.source.remote

import com.fulbiopretell.retoyape.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class HelperWs {

    companion object {

        fun getConfigurationServiceData(): Retrofit {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .readTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.URL_SERVICE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build()


        }

        fun getServiceData(): WebServiceData {
            return getConfigurationServiceData().create(WebServiceData::class.java)
        }
    }
}
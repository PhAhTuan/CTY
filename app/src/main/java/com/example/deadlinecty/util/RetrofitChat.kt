package com.example.deadlinecty.util

import okhttp3.OkHttpClient
import kotlin.jvm.java
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
object RetrofitclientChat {
    object RetrofitClient {
        private const val BASE_URL = "https://api.gateway.overate-vntech.com/"

        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("ProjectId", "9024")
                    .addHeader("Authorization", "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0")
                    .addHeader("Method", "0")
                    .build()
                chain.proceed(request)
            }
            .build()
        val apiServicechat: ChatApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChatApiService::class.java)
        }
    }
}

object RetrofitPhoto {
    private const val BASE_URL = "https://api.gateway.overate-vntech.com/"
    val retrofitMedia: MediaService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MediaService::class.java)
    }
}



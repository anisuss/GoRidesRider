package com.goridesnigeria.goridesrider.data.api

import android.app.Activity
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ApiClient {

    var BASE_URL = "https://gorides.ucstestserver.xyz/api/"

    private var retrofit: Retrofit? = null
    fun getClient(activity: Activity): Retrofit? {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val builder = OkHttpClient.Builder()
            builder.readTimeout(1,TimeUnit.MINUTES)
            builder.writeTimeout(1,TimeUnit.MINUTES)
            builder.connectTimeout(1,TimeUnit.MINUTES)
            builder.interceptors().add(loggingInterceptor)
            val client = builder.build()
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }
}
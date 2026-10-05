package com.ecommerce.ecom.Api

import android.content.Context
import com.ecommerce.ecom.Utils.NetworkHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppModule {

    companion object {

        fun provideProductApi(): Api {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


            return retrofit.create(Api::class.java)
        }

        fun provideNetworkHelper(context: Context): NetworkHelper {
            return NetworkHelper(context)
        }

    }
}
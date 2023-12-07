package com.example.sg.data

import android.content.Context
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface ApiService {

    @Multipart
    @POST("ecom/logo/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response

    companion object {
        private const val BASE_URL = "https://reqres.in/api/"

        fun create(context: Context): ApiService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .readTimeout(120L, TimeUnit.SECONDS)
                .writeTimeout(120L, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
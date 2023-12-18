package com.example.sg.data

import okhttp3.Interceptor
import okhttp3.Response

class OAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // val token = privateSharedPrefManager.getStringFromSharedPreferences(ACCESS_TOKEN)
        val token = "5b22851a232b37ce40edbbe2242375d78d86f77df74b872a9ec2db725ff955d6"
        var request = chain.request()
        request =
            request.newBuilder().header("Authorization", "Bearer $token").build()
        return chain.proceed(request)
    }
}
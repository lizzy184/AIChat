package com.example.aichatapp.di

import com.example.aichatapp.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val token = runBlocking {
            userPreferences.accessToken.first()
        }

        val request = chain.request()
            .newBuilder()
            .apply {

                if (!token.isNullOrEmpty()) {

                    addHeader(
                        "Authorization",
                        "Bearer $token"
                    )

                }

            }
            .build()

        return chain.proceed(request)
    }
}
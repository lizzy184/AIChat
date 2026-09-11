package com.example.aichatapp.di


import com.example.aichatapp.BuildConfig
import com.example.aichatapp.network.ChatApi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import javax.inject.Singleton

import java.util.concurrent.TimeUnit

import android.content.Context

import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.aichatapp.data.UserPreferences
import com.example.aichatapp.network.RefreshApi
import com.google.gson.Gson


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        userPreferences: UserPreferences,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {

        val logging =
            HttpLoggingInterceptor()

        logging.level =
            HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()

            .addInterceptor(
                AuthInterceptor(userPreferences)
            )
            .authenticator(
                tokenAuthenticator
            )
            .addInterceptor(logging)

            .connectTimeout(
                60,
                TimeUnit.SECONDS
            )

            .readTimeout(
                120,
                TimeUnit.SECONDS
            )

            .writeTimeout(
                60,
                TimeUnit.SECONDS
            )

            .build()
    }

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(
        @RefreshClient client: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {


        return Retrofit.Builder()

            .baseUrl(
                BuildConfig.BASE_URL
            )


            .client(
                okHttpClient
            )


            .addConverterFactory(
                GsonConverterFactory.create()
            )


            .build()


    }





    @Provides
    @Singleton
    fun provideChatApi(
        retrofit: Retrofit
    ): ChatApi {


        return retrofit.create(
            ChatApi::class.java
        )

    }

    @Provides
    @Singleton
    fun provideRefreshApi(
        @RefreshRetrofit retrofit: Retrofit
    ): RefreshApi {
        return retrofit.create(RefreshApi::class.java)
    }



    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences{


        return UserPreferences(
            context
        )


    }


}
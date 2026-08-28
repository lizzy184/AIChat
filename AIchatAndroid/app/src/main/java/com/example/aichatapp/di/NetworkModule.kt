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



@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {



    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {


        val logging =
            HttpLoggingInterceptor()


        logging.level =
            HttpLoggingInterceptor.Level.BODY



        return OkHttpClient.Builder()

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
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences{


        return UserPreferences(
            context
        )


    }


}
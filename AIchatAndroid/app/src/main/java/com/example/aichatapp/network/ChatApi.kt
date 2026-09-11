package com.example.aichatapp.network
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

import com.example.aichatapp.model.ChatRequest
import com.example.aichatapp.model.ChatResponse
import com.example.aichatapp.model.HistoryMessage
import com.example.aichatapp.model.LoginRequest
import com.example.aichatapp.model.LoginResponse
import com.example.aichatapp.model.RefreshRequest
import com.example.aichatapp.model.RefreshResponse
import com.example.aichatapp.model.RegisterRequest
import com.example.aichatapp.model.RegisterResponse

import retrofit2.Response

import retrofit2.http.DELETE


interface ChatApi {

    @POST("chat")
    suspend fun chat(
        @Body request:ChatRequest
    ):ChatResponse

    @GET("history")
    suspend fun getHistory( ):

            List<HistoryMessage>


    @DELETE("history")
    suspend fun clearHistory():
            Response<Unit>

    @POST("/register")
    suspend fun register(

        @Body request: RegisterRequest

    ): RegisterResponse

@POST("/login")
suspend fun login(
    @Body request: LoginRequest

): LoginResponse

}
interface RefreshApi {

    @POST("refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): RefreshResponse
}
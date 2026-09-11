package com.example.aichatapp.repository



import com.example.aichatapp.data.UserPreferences
import com.example.aichatapp.model.Message
import com.example.aichatapp.model.ChatRequest
import com.example.aichatapp.model.LoginRequest
import com.example.aichatapp.model.LoginResponse
import com.example.aichatapp.model.RegisterRequest
import com.example.aichatapp.model.RegisterResponse
import com.example.aichatapp.network.ChatApi
import com.example.aichatapp.network.NetworkResult
import java.net.UnknownHostException


import retrofit2.HttpException

import java.io.IOException

import javax.inject.Inject



class ChatRepository @Inject constructor(

    private val api: ChatApi,
    private val userPreferences: UserPreferences

) {



    /**
     *
     * 发送消息
     *
     * Android
     *      |
     *      ↓
     * POST /chat
     *
     * FastAPI负责:
     *
     * 1. 保存用户消息
     *
     * 2. 调用DeepSeek
     *
     * 3. 保存AI回复
     *
     * 4. 返回结果
     *
     */
    suspend fun getAiReply(
        message: String

    ): NetworkResult<Message> {


        return try {



            val response = api.chat(

                ChatRequest(


                    message = message

                )

            )



            val aiMessage = Message(

                text = response.reply,

                isUser = false,

                time = ""

            )



            NetworkResult.Success(

                aiMessage

            )


        } catch (e: IOException) {


            NetworkResult.Error(

                "网络连接失败"

            )


        } catch (e: HttpException) {


            val errorBody =

                e.response()
                    ?.errorBody()
                    ?.string()



            NetworkResult.Error(

                "服务器错误:${e.code()} $errorBody"

            )


        } catch (e: Exception) {


            e.printStackTrace()


            NetworkResult.Error(

                "未知错误:${e.message}"

            )


        }


    }





    /**
     *
     * 获取聊天历史
     *
     * Android
     *
     * GET /history
     *
     * ↓
     *
     * FastAPI
     *
     * ↓
     *
     * SQLite
     *
     * ↓
     *
     * 返回历史
     *
     */
    suspend fun getHistory(

    ):

            NetworkResult<List<Message>> {


        return try {


            val response = api.getHistory()



            val messages = response.map {


                Message(

                    text = it.content,

                    isUser =

                        it.role == "user",


                    time = it.created_time

                )


            }



            NetworkResult.Success(

                messages

            )


        } catch (e: IOException) {


            NetworkResult.Error(

                "网络连接失败"

            )


        } catch (e: HttpException) {


            NetworkResult.Error(

                "服务器错误:${e.code()}"

            )


        } catch (e: Exception) {


            NetworkResult.Error(

                "未知错误:${e.message}"

            )


        }


    }

    suspend fun clearHistory():
            NetworkResult<Unit>{


        return try {


            val response =
                api.clearHistory()



            if(response.isSuccessful){


                NetworkResult.Success(
                    Unit
                )


            }else{


                NetworkResult.Error(
                    "删除失败:${response.code()}"
                )


            }



        }catch(e:IOException){


            NetworkResult.Error(
                "网络连接失败"
            )


        }catch(e:Exception){


            NetworkResult.Error(
                e.message ?: "未知错误"
            )

        }

    }

    suspend fun register(
        username: String,
        password: String
    ): NetworkResult<RegisterResponse> {

        return try {

            val response = api.register(
                RegisterRequest(
                    username = username,
                    password = password
                )
            )

            NetworkResult.Success(response)

        } catch (e: HttpException) {

            when (e.code()) {

                409 -> NetworkResult.Error(
                    "用户名已存在",
                    409
                )

                else -> NetworkResult.Error(
                    "注册失败，请稍后重试",
                    e.code()
                )
            }

        } catch (e: UnknownHostException) {

            NetworkResult.Error(
                "无法连接服务器，请检查网络"
            )

        } catch (e: IOException) {

            NetworkResult.Error(
                "网络连接失败，请稍后重试"
            )

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "注册失败"
            )
        }
    }

   suspend fun login(
        username: String,
        password: String
    ): NetworkResult<LoginResponse> {
        return try {

            val response = api.login(
                LoginRequest(username, password)
            )

            NetworkResult.Success(response)

        } catch (e: retrofit2.HttpException) {

            when (e.code()) {
                401 -> NetworkResult.Error(
                    "用户名或密码错误",
                    401
                )

                else -> NetworkResult.Error(
                    "登录失败，请稍后重试",
                    e.code()
                )
            }

        } catch (e: java.net.UnknownHostException) {

            NetworkResult.Error("无法连接服务器，请检查网络")

        } catch (e: java.io.IOException) {

            NetworkResult.Error("网络连接失败，请稍后重试")

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "登录失败"
            )
        }
    }

}
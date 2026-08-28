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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import retrofit2.HttpException

import java.io.IOException

import javax.inject.Inject
import kotlinx.coroutines.flow.first


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
            val userId =

                userPreferences.userId.first()
                    ?: 0


            val response = api.chat(

                ChatRequest(
                    user_id = userId,

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
        userId:Int
    ):

            NetworkResult<List<Message>> {


        return try {


            val response = api.getHistory(userId )



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
        username:String,
        password: String
    ):NetworkResult<RegisterResponse>{


        return try{


            val response =
                api.register(
                    RegisterRequest(password ,username)
                )


            NetworkResult.Success(
                response
            )


        }catch(e:Exception){


            NetworkResult.Error(
                e.message ?: "注册失败"
            )

        }


    }

    suspend fun login(
        username: String,
        password: String
    ): NetworkResult<LoginResponse>{
return try {

    val response=
    api.login(LoginRequest(username, password ))

    NetworkResult.Success(response)

}catch (e: Exception){
    NetworkResult.Error(
        e.message?:"登录失败"
    )


}


    }

}
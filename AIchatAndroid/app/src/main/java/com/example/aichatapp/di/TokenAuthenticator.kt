package com.example.aichatapp.di

import com.example.aichatapp.data.UserPreferences
import com.example.aichatapp.model.RefreshRequest
import com.example.aichatapp.network.RefreshApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val userPreferences: UserPreferences,
    private val refreshApi: RefreshApi
) : Authenticator {

    /**
     * 防止多个请求同时刷新 Token
     */
    private val refreshLock = Any()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {

        /**
         * 防止无限重试
         */
        if (responseCount(response) >= 2) {
            return null
        }

        /**
         * 获取本次请求使用的旧 Access Token
         */
        val failedToken =
            response.request
                .header("Authorization")
                ?.removePrefix("Bearer ")

        /**
         * 获取 DataStore 当前保存的 Access Token
         */
        val currentToken = runBlocking {
            userPreferences.accessToken.first()
        }

        /**
         * 如果当前 Token 和失败请求中的 Token 不一样，
         * 说明可能已经有其他请求刷新成功。
         *
         * 直接使用新的 Token 重试，不需要再次 Refresh。
         */
        if (
            !currentToken.isNullOrEmpty() &&
            currentToken != failedToken
        ) {
            return response.request
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer $currentToken"
                )
                .build()
        }

        /**
         * 开始执行真正的 Refresh。
         *
         * 同一时间只允许一个线程进入这里。
         */
        synchronized(refreshLock) {

            /**
             * 重新读取最新 Access Token。
             *
             * 因为当前线程可能刚刚等待过锁，
             * 在等待期间其他线程可能已经完成 Refresh。
             */
            val latestToken = runBlocking {
                userPreferences.accessToken.first()
            }

            /**
             * 如果 Token 已经发生变化，
             * 说明其他线程已经 Refresh 成功。
             *
             * 直接使用新的 Token。
             */
            if (
                !latestToken.isNullOrEmpty() &&
                latestToken != failedToken
            ) {
                return response.request
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer $latestToken"
                    )
                    .build()
            }

            /**
             * 获取 Refresh Token
             */
            val refreshToken = runBlocking {
                userPreferences.refreshToken.first()
            }

            /**
             * 没有 Refresh Token，
             * 无法继续刷新。
             */
            if (refreshToken.isNullOrEmpty()) {
                return null
            }

            /**
             * 调用后端 /refresh
             */
            val refreshResponse = try {

                runBlocking {
                    refreshApi.refresh(
                        RefreshRequest(
                            refresh_token = refreshToken
                        )
                    )
                }

            } catch (e: Exception) {

                /**
                 * Refresh 失败：
                 * 清除 Access Token 和 Refresh Token。
                 */
                runBlocking {
                    userPreferences.clearTokens()
                }

                return null
            }

            /**
             * 保存新的 Access Token
             *
             * 注意：
             * Refresh Token 不变，所以这里只更新 Access Token。
             */
            runBlocking {
                userPreferences.saveAccessToken(
                    refreshResponse.access_token
                )
            }

            /**
             * 使用新的 Access Token
             * 重新发送刚才失败的请求。
             */
            return response.request
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${refreshResponse.access_token}"
                )
                .build()
        }
    }

    /**
     * 计算当前请求已经重试了多少次。
     */
    private fun responseCount(response: Response): Int {

        var count = 1

        var priorResponse = response.priorResponse

        while (priorResponse != null) {

            count++

            priorResponse =
                priorResponse.priorResponse
        }

        return count
    }
}
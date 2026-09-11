package com.example.aichatapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_info"
)

class UserPreferences(
    private val context: Context
) {

    companion object {

        private val ACCESS_TOKEN =
            stringPreferencesKey("access_token")
        private val REFRESH_TOKEN =
            stringPreferencesKey("refresh_token")
        private val HAS_REGISTERED =
            booleanPreferencesKey("has_registered")
    }

    /**
     * 保存 JWT
     */
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveAccessToken(


        token: String
    ){
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN]=token



        }



    }


    /**
     * 获取 JWT
     */
    val accessToken: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    val refreshToken: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }
    /**
     * 是否注册过
     */
    val hasRegistered: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[HAS_REGISTERED] ?: false
        }

    suspend fun saveHasRegistered() {
        context.dataStore.edit { preferences ->
            preferences[HAS_REGISTERED] = true
        }
    }

    /**
     * 清除 JWT
     */
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)

    }
    }
}
package com.example.aichatapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey

import androidx.datastore.preferences.core.intPreferencesKey

import androidx.datastore.preferences.core.edit

import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.map



private val Context.dataStore by preferencesDataStore(
    name = "user_info"
)



class UserPreferences(
    private val context: Context
){



    companion object{


        private val USER_ID =
            intPreferencesKey(
                "user_id"
            )
        private val HAS_REGISTERED =
            booleanPreferencesKey(
                "has_registered"
            )
    }





    /**
     * 保存userid
     */
    suspend fun saveUserId(
        userId:Int
    ){


        context.dataStore.edit {


                preferences ->


            preferences[USER_ID]=userId


        }

    }





    /**
     * 获取userid
     */
    val userId:Flow<Int?> =


        context.dataStore.data.map {


                preferences ->


            preferences[USER_ID]


        }
    val hasRegistered:Flow<Boolean> =


        context.dataStore.data.map {


                preferences ->


            preferences[HAS_REGISTERED] ?: false


        }

    suspend fun saveHasRegistered(){

        context.dataStore.edit {

                preferences ->

            preferences[HAS_REGISTERED] = true

        }

    }


    suspend fun clearUserId()
    {
context.dataStore.edit {
    preferences ->
    preferences.remove(USER_ID)


}



    }

}
package com.example.aichatapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

 @Insert
 suspend fun insertMessage(message: MessageEntity)


 @Query( "SELECT * FROM messages ORDER BY id ASC")
 fun getAllMessages():
         Flow<List<MessageEntity>>

 @Query( "SELECT * FROM messages ORDER BY id ASC")
 suspend fun getMessagesOnce():List<MessageEntity>

@Query("DELETE FROM messages")
suspend fun clearMessages()



}
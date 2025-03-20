package gg.stephen.gptwrapper.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {
    @Query("SELECT * FROM chat_histories ORDER BY timestamp DESC")
    fun getAllChatHistories(): Flow<List<ChatHistory>>

    @Insert
    suspend fun insertChatHistory(chatHistory: ChatHistory): Long

    @Update
    suspend fun updateChatHistory(chatHistory: ChatHistory)

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistory)

    @Query("DELETE FROM chat_histories")
    suspend fun deleteAllChatHistories()

    @Query("SELECT * FROM chat_histories WHERE id = :id")
    suspend fun getChatHistoryById(id: Long): ChatHistory?
} 
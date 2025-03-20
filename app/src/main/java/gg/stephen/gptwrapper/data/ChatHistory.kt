package gg.stephen.gptwrapper.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import gg.stephen.gptwrapper.chat.ChatItem
import gg.stephen.gptwrapper.chat.User
import java.util.Date

@Entity(tableName = "chat_histories")
data class ChatHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val lastMessage: String,
    val timestamp: Date,
    val model: String,
    @TypeConverters(ChatItemListConverter::class)
    val conversation: List<ChatItem>
)

class ChatItemListConverter {
    @androidx.room.TypeConverter
    fun fromString(value: String): List<ChatItem> {
        return value.split("|").map { item ->
            val (user, message) = item.split(":")
            ChatItem(User.valueOf(user), message)
        }
    }

    @androidx.room.TypeConverter
    fun toString(items: List<ChatItem>): String {
        return items.joinToString("|") { "${it.user}:${it.message}" }
    }
} 
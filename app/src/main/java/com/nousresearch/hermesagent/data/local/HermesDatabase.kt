package com.nousresearch.hermesagent.data.local

import android.content.Context
import androidx.room.*

@Database(entities = [MessageEntity::class, SessionEntity::class], version = 1, exportSchema = false)
abstract class HermesDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: HermesDatabase? = null

        fun getInstance(context: Context): HermesDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HermesDatabase::class.java,
                    "hermes_agent_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

// ── Entities ──
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "session_id") val sessionId: String,
    val role: String,        // "user", "assistant", "system", "tool"
    val content: String,
    @ColumnInfo(name = "tool_name") val toolName: String? = null,
    @ColumnInfo(name = "tool_args") val toolArgs: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val title: String = "Новая сессия",
    val model: String? = null,
    @ColumnInfo(name = "provider") val provider: String? = null,
    @ColumnInfo(name = "message_count") val messageCount: Int = 0,
    @ColumnInfo(name = "is_active") val isActive: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
)

// ── DAOs ──
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE session_id = :sessionId ORDER BY created_at ASC")
    fun getMessages(sessionId: String): kotlinx.coroutines.flow.Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE session_id = :sessionId ORDER BY created_at ASC")
    suspend fun getMessagesSync(sessionId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE session_id = :sessionId")
    suspend fun deleteSessionMessages(sessionId: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY updated_at DESC")
    fun getAllSessions(): kotlinx.coroutines.flow.Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions ORDER BY updated_at DESC LIMIT 1")
    suspend fun getLatestSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("UPDATE sessions SET is_active = 0")
    suspend fun deactivateAllSessions()

    @Query("UPDATE sessions SET is_active = 1 WHERE id = :sessionId")
    suspend fun activateSession(sessionId: String)

    @Query("UPDATE sessions SET title = :title WHERE id = :sessionId")
    suspend fun renameSession(sessionId: String, title: String)

    @Query("UPDATE sessions SET message_count = message_count + 1 WHERE id = :sessionId")
    suspend fun incrementMessageCount(sessionId: String)

    @Query("UPDATE sessions SET updated_at = :timestamp WHERE id = :sessionId")
    suspend fun updateTimestamp(sessionId: String, timestamp: Long)

    @Delete
    suspend fun deleteSession(session: SessionEntity)

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}

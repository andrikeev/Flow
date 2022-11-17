package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.HistoryTopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [HistoryTopicEntity] access.
 */
@Dao
interface HistoryTopicDao {
    /**
     * Observe all [HistoryTopicEntity]s in order from newest to latest.
     */
    @Query("SELECT * FROM HistoryTopic ORDER by timestamp DESC")
    fun observerAll(): Flow<List<HistoryTopicEntity>>

    /**
     * Observe all [HistoryTopicEntity]'s ids.
     */
    @Query("SELECT id FROM HistoryTopic")
    fun observerAllIds(): Flow<List<String>>

    /**
     * Insert new [HistoryTopicEntity] or replace existed.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryTopicEntity)

    /**
     * Clear all [HistoryTopicEntity]s.
     */
    @Query("DELETE FROM HistoryTopic")
    suspend fun deleteAll()
}

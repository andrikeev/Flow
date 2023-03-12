package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.VisitedTopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [VisitedTopicEntity] access.
 */
@Dao
interface VisitedTopicDao {
    /**
     * Observe all [VisitedTopicEntity]s in order from newest to latest.
     */
    @Query("SELECT * FROM HistoryTopic ORDER by timestamp DESC")
    fun observerAll(): Flow<List<VisitedTopicEntity>>

    /**
     * Observe all [VisitedTopicEntity]'s ids.
     */
    @Query("SELECT id FROM HistoryTopic")
    fun observerAllIds(): Flow<List<String>>

    /**
     * Insert new [VisitedTopicEntity] or replace existed.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VisitedTopicEntity)

    /**
     * Clear all [VisitedTopicEntity]s.
     */
    @Query("DELETE FROM HistoryTopic")
    suspend fun deleteAll()
}

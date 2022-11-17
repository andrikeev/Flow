package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [SearchHistoryEntity] access.
 */
@Dao
interface SearchHistoryDao {
    /**
     * Observe all [SearchHistoryEntity]s in order from newest to latest.
     */
    @Query("SELECT * FROM Search ORDER by timestamp DESC")
    fun observerAll(): Flow<List<SearchHistoryEntity>>

    /**
     * Insert new [SearchHistoryEntity] or replace existed.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SearchHistoryEntity)

    /**
     * Clear all [SearchHistoryEntity]s.
     */
    @Query("DELETE FROM Search")
    suspend fun deleteAll()
}

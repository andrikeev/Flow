package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.SuggestEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [SuggestEntity] access.
 */
@Dao
interface SuggestDao {
    /**
     * Observe all [SuggestEntity]s in order from newest to latest.
     */
    @Query("SELECT * FROM Suggest ORDER by timestamp DESC")
    fun observerAll(): Flow<List<SuggestEntity>>

    /**
     * Insert new [SuggestEntity] or replace existed.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SuggestEntity)

    /**
     * Clear all [SuggestEntity]s.
     */
    @Query("DELETE FROM Suggest")
    suspend fun deleteAll()
}

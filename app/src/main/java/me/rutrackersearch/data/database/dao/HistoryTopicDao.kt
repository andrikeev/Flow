package me.rutrackersearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.data.database.entity.HistoryTopicEntity

@Dao
interface HistoryTopicDao {
    @Query("SELECT * FROM HistoryTopic ORDER by timestamp DESC")
    fun observerAll(): Flow<List<HistoryTopicEntity>>

    @Query("SELECT id FROM HistoryTopic")
    fun observerAllIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryTopicEntity)

    @Query("DELETE FROM HistoryTopic")
    suspend fun deleteAll()
}

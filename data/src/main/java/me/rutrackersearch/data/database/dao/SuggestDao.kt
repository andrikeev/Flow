package me.rutrackersearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.data.database.entity.SuggestEntity

@Dao
interface SuggestDao {
    @Query("SELECT * FROM Suggest ORDER by timestamp DESC")
    fun observerAll(): Flow<List<SuggestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SuggestEntity)

    @Query("DELETE FROM Suggest")
    suspend fun deleteAll()
}

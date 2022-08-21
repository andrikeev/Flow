package me.rutrackersearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity

@Dao
interface FavoriteTopicDao {
    @Query("SELECT * FROM FavoriteTopic ORDER by timestamp DESC")
    fun observerAll(): Flow<List<FavoriteTopicEntity>>

    @Query("SELECT id FROM FavoriteTopic")
    fun observerAllIds(): Flow<List<String>>

    @Query("SELECT id FROM FavoriteTopic WHERE hasUpdate")
    fun observerUpdatedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteTopicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<FavoriteTopicEntity>)

    @Query("DELETE FROM FavoriteTopic WHERE id == :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE FavoriteTopic SET hasUpdate = 0 WHERE id == :id")
    suspend fun update(id: String)

    @Query("DELETE FROM FavoriteTopic")
    suspend fun deleteAll()
}

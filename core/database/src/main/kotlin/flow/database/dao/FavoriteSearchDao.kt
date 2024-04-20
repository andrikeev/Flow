package flow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.FavoriteSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSearchDao {
    @Query("SELECT * FROM FavoriteSearch")
    fun observerAll(): Flow<List<FavoriteSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteSearchEntity)

    @Delete
    suspend fun delete(entity: FavoriteSearchEntity)

    @Query("DELETE FROM FavoriteSearch")
    suspend fun deleteAll()
}

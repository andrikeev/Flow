package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.EndpointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EndpointDao {
    @Query("SELECT (SELECT COUNT(*) FROM Endpoint) == 0")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM Endpoint")
    fun observerAll(): Flow<List<EndpointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(endpoints: List<EndpointEntity>)
}

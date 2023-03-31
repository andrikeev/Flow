package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.ForumMetadata

@Dao
interface ForumMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(metadata: ForumMetadata)

    @Query("SELECT * FROM ForumMetadata WHERE id = 1")
    suspend fun getMetadata(): ForumMetadata?
}

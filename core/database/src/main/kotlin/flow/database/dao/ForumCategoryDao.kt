package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.ForumCategoryEntity

@Dao
interface ForumCategoryDao {
    @Query("SELECT EXISTS (SELECT 1 FROM ForumCategoryEntity)")
    suspend fun isForumStored(): Boolean

    @Query("SELECT * FROM ForumCategoryEntity WHERE id = :id")
    suspend fun get(id: String): ForumCategoryEntity?

    @Query("SELECT * FROM ForumCategoryEntity WHERE parentId IS NULL")
    suspend fun getTopLevelCategories(): List<ForumCategoryEntity>

    @Query("SELECT * FROM ForumCategoryEntity WHERE parentId = :parentId")
    suspend fun getChildren(parentId: String): List<ForumCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<ForumCategoryEntity>)

    @Query("DELETE FROM ForumCategoryEntity")
    suspend fun deleteAll()
}

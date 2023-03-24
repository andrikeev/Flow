package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [BookmarkEntity] access.
 */
@Dao
interface BookmarkDao {
    /**
     * Get list of all [BookmarkEntity]s in order from newest to latest.
     */
    @Query("SELECT * FROM Bookmark ORDER by timestamp DESC")
    suspend fun getAll(): List<BookmarkEntity>

    /**
     * Observe all [BookmarkEntity]s in order from newest to latest.
     */
    @Query("SELECT * FROM Bookmark ORDER by timestamp DESC")
    fun observerAll(): Flow<List<BookmarkEntity>>

    /**
     * Get [BookmarkEntity] with provided id.
     *
     * @param id [BookmarkEntity.id]
     */
    @Query("SELECT * FROM Bookmark WHERE id == :id")
    suspend fun get(id: String): BookmarkEntity?

    /**
     * Observe all [BookmarkEntity]'s ids.
     */
    @Query("SELECT id FROM Bookmark")
    fun observerIds(): Flow<List<String>>

    /**
     * Observe new topics ids in [BookmarkEntity] with provided id.
     *
     * @param id [BookmarkEntity.id]
     * @see BookmarkEntity.newTopics
     */
    @Query("SELECT newTopics FROM Bookmark WHERE id == :id")
    fun observeNewTopics(id: String): Flow<List<String>>

    /**
     * Observe new topics ids in all [BookmarkEntity]s.
     *
     * @see BookmarkEntity.newTopics
     */
    @Query("SELECT newTopics FROM Bookmark")
    fun observeNewTopics(): Flow<List<String>>

    /**
     * Insert new [BookmarkEntity] or replace existed.
     *
     * @param entity [BookmarkEntity] to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BookmarkEntity)

    /**
     * Remove [BookmarkEntity] with provided id.
     *
     * @param id [BookmarkEntity.id] of entity to delete
     */
    @Query("DELETE FROM Bookmark WHERE id == :id")
    suspend fun deleteById(id: String)

    /**
     * Clear all [BookmarkEntity]s.
     */
    @Query("DELETE FROM Bookmark")
    suspend fun deleteAll()

    /**
     * Update [BookmarkEntity] with provided id and clear [BookmarkEntity.newTopics] list.
     *
     * @param id [BookmarkEntity.id] of entity to update
     * @see BookmarkEntity.newTopics
     */
    @Query("UPDATE Bookmark SET newTopics = :empty WHERE id == :id")
    suspend fun markVisited(id: String, empty: String = "[]")
}

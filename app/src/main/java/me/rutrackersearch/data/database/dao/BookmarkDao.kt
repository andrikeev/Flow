package me.rutrackersearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.data.database.entity.BookmarkEntity

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM Bookmark ORDER by timestamp DESC")
    fun getAll(): List<BookmarkEntity>

    @Query("SELECT * FROM Bookmark ORDER by timestamp DESC")
    fun observerAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM Bookmark WHERE id == :id")
    fun getById(id: String): BookmarkEntity?

    @Query("SELECT id FROM Bookmark")
    fun observerIds(): Flow<List<String>>

    @Query("SELECT newTopics FROM Bookmark WHERE id == :id")
    fun observeNewTopics(id: String): Flow<List<String>>

    @Query("SELECT newTopics FROM Bookmark")
    fun observeNewTopics(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BookmarkEntity)

    @Query("DELETE FROM Bookmark WHERE id == :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM Bookmark")
    suspend fun deleteAll()

    @Query("UPDATE Bookmark SET newTopics = :empty WHERE id == :id")
    suspend fun markVisited(id: String, empty: String = "[]")
}

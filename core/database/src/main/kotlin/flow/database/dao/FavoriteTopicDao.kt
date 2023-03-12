package flow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import flow.database.entity.FavoriteTopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [FavoriteTopicEntity] access.
 */
@Dao
interface FavoriteTopicDao {
    /**
     * Get all [FavoriteTopicEntity]'s ids.
     */
    @Query("SELECT id FROM FavoriteTopic")
    suspend fun getAllIds(): List<String>

    /**
     * Get all [FavoriteTopicEntity]s.
     */
    @Query("SELECT * FROM FavoriteTopic")
    suspend fun getAll(): List<FavoriteTopicEntity>

    /**
     * Observe all [FavoriteTopicEntity]s from newest to latest.
     */
    @Query("SELECT * FROM FavoriteTopic ORDER by timestamp DESC")
    fun observerAll(): Flow<List<FavoriteTopicEntity>>

    /**
     * Observe all [FavoriteTopicEntity]'s ids.
     */
    @Query("SELECT id FROM FavoriteTopic")
    fun observerAllIds(): Flow<List<String>>

    /**
     * Observe [FavoriteTopicEntity]'s ids that has updates.
     *
     * @see FavoriteTopicEntity.hasUpdate
     */
    @Query("SELECT id FROM FavoriteTopic WHERE hasUpdate")
    fun observerUpdatedIds(): Flow<List<String>>

    /**
     * Insert new [FavoriteTopicEntity] or replace existed.
     *
     * @param entity [FavoriteTopicEntity] to insert or replace
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteTopicEntity)

    /**
     * Insert new [FavoriteTopicEntity]s or replace existed.
     *
     * @param entities list of [FavoriteTopicEntity] to insert or replace
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: Collection<FavoriteTopicEntity>)

    /**
     * Remove [FavoriteTopicEntity] with provided id.
     *
     * @param id [FavoriteTopicEntity.id] of entity to delete
     */
    @Query("DELETE FROM FavoriteTopic WHERE id == :id")
    suspend fun delete(id: String)

    /**
     * Remove [FavoriteTopicEntity]s with provided ids.
     *
     * @param ids list of [FavoriteTopicEntity.id] of entities to delete
     */
    @Query("DELETE FROM FavoriteTopic WHERE id IN (:ids)")
    suspend fun delete(ids: Collection<String>)

    /**
     * Update [FavoriteTopicEntity] with provided id and set [FavoriteTopicEntity.hasUpdate] to false.
     *
     * @param id [FavoriteTopicEntity.id] of entity to update
     * @see FavoriteTopicEntity.hasUpdate
     */
    @Query("UPDATE FavoriteTopic SET hasUpdate = 0 WHERE id == :id")
    suspend fun update(id: String)

    /**
     * Clear all [FavoriteTopicEntity]s.
     */
    @Query("DELETE FROM FavoriteTopic")
    suspend fun deleteAll()
}

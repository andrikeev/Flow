package flow.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import flow.database.converters.Converters
import flow.database.dao.BookmarkDao
import flow.database.dao.EndpointDao
import flow.database.dao.FavoriteSearchDao
import flow.database.dao.FavoriteTopicDao
import flow.database.dao.ForumCategoryDao
import flow.database.dao.ForumMetadataDao
import flow.database.dao.SearchHistoryDao
import flow.database.dao.SuggestDao
import flow.database.dao.VisitedTopicDao
import flow.database.entity.BookmarkEntity
import flow.database.entity.EndpointEntity
import flow.database.entity.FavoriteSearchEntity
import flow.database.entity.FavoriteTopicEntity
import flow.database.entity.ForumCategoryEntity
import flow.database.entity.ForumMetadata
import flow.database.entity.SearchHistoryEntity
import flow.database.entity.SuggestEntity
import flow.database.entity.VisitedTopicEntity

@Database(
    entities = [
        BookmarkEntity::class,
        EndpointEntity::class,
        FavoriteSearchEntity::class,
        FavoriteTopicEntity::class,
        ForumCategoryEntity::class,
        ForumMetadata::class,
        SearchHistoryEntity::class,
        SuggestEntity::class,
        VisitedTopicEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun endpointDao(): EndpointDao
    abstract fun favoriteTopicDao(): FavoriteTopicDao
    abstract fun favoritesSearchDao(): FavoriteSearchDao
    abstract fun forumCategoryDao(): ForumCategoryDao
    abstract fun forumMetadataDao(): ForumMetadataDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun suggestDao(): SuggestDao
    abstract fun visitedTopicDao(): VisitedTopicDao
}

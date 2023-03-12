package flow.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import flow.database.converters.Converters
import flow.database.dao.BookmarkDao
import flow.database.dao.FavoriteTopicDao
import flow.database.dao.SearchHistoryDao
import flow.database.dao.SuggestDao
import flow.database.dao.VisitedTopicDao
import flow.database.entity.BookmarkEntity
import flow.database.entity.FavoriteTopicEntity
import flow.database.entity.SearchHistoryEntity
import flow.database.entity.SuggestEntity
import flow.database.entity.VisitedTopicEntity

@Database(
    entities = [
        SuggestEntity::class,
        SearchHistoryEntity::class,
        VisitedTopicEntity::class,
        FavoriteTopicEntity::class,
        BookmarkEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun suggestDao(): SuggestDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun visitedTopicDao(): VisitedTopicDao
    abstract fun favoriteTopicDao(): FavoriteTopicDao
    abstract fun bookmarkDao(): BookmarkDao
}

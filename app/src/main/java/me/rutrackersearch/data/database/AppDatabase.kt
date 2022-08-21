package me.rutrackersearch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.rutrackersearch.data.database.converters.Converters
import me.rutrackersearch.data.database.dao.BookmarkDao
import me.rutrackersearch.data.database.dao.FavoriteTopicDao
import me.rutrackersearch.data.database.dao.HistoryTopicDao
import me.rutrackersearch.data.database.dao.SearchHistoryDao
import me.rutrackersearch.data.database.dao.SuggestDao
import me.rutrackersearch.data.database.entity.BookmarkEntity
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity
import me.rutrackersearch.data.database.entity.HistoryTopicEntity
import me.rutrackersearch.data.database.entity.SearchHistoryEntity
import me.rutrackersearch.data.database.entity.SuggestEntity

@Database(
    entities = [
        SuggestEntity::class,
        SearchHistoryEntity::class,
        HistoryTopicEntity::class,
        FavoriteTopicEntity::class,
        BookmarkEntity::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun suggestDao(): SuggestDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun historyTopicDao(): HistoryTopicDao
    abstract fun favoriteTopicDao(): FavoriteTopicDao
    abstract fun bookmarkDao(): BookmarkDao
}

package flow.database.di

import androidx.room.Room
import flow.database.AppDatabase
import flow.database.AppDatabase.Companion.MIGRATION_3_4
import flow.database.AppDatabase.Companion.MIGRATION_4_5
import flow.database.AppDatabase.Companion.MIGRATION_5_6
import org.koin.dsl.module

/**
 * Koin module for the Room database and its DAOs. The Context is resolved from the Koin
 * graph (androidContext()). On Android the DAOs are exposed to remaining Hilt consumers
 * (core:data) via an inverse-bridge in :app until they migrate to Koin.
 */
val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(get(), AppDatabase::class.java, "flow-db")
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addMigrations(MIGRATION_5_6)
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single { get<AppDatabase>().bookmarkDao() }
    single { get<AppDatabase>().favoritesSearchDao() }
    single { get<AppDatabase>().favoriteTopicDao() }
    single { get<AppDatabase>().forumCategoryDao() }
    single { get<AppDatabase>().forumMetadataDao() }
    single { get<AppDatabase>().searchHistoryDao() }
    single { get<AppDatabase>().suggestDao() }
    single { get<AppDatabase>().visitedTopicDao() }
}

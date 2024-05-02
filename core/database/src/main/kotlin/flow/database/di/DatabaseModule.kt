package flow.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import flow.database.AppDatabase
import flow.database.AppDatabase.Companion.MIGRATION_3_4
import flow.database.AppDatabase.Companion.MIGRATION_4_5
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "flow-db",
        )
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build()
}

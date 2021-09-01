package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.DatabaseFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(factory: DatabaseFactory): AppDatabase {
        return factory.get()
    }
}

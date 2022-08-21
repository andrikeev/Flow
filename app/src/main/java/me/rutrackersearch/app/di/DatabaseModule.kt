package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.DatabaseFactory
import me.rutrackersearch.data.database.DatabaseFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {
    @Binds
    @Singleton
    fun databaseFactory(impl: DatabaseFactoryImpl): DatabaseFactory

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(factory: DatabaseFactory): AppDatabase = factory.get()
    }
}

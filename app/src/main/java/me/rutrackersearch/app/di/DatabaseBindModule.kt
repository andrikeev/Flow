package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.database.DatabaseFactory
import me.rutrackersearch.data.database.DatabaseFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseBindModule {
    @Binds
    @Singleton
    fun databaseFactory(impl: DatabaseFactoryImpl): DatabaseFactory
}

package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.sync.BookmarksSyncServiceImpl
import me.rutrackersearch.data.sync.FavoritesSyncServiceImpl
import me.rutrackersearch.domain.service.BookmarksSyncService
import me.rutrackersearch.domain.service.FavoritesSyncService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SyncBindModule {
    @Binds
    @Singleton
    fun favoritesSyncService(impl: FavoritesSyncServiceImpl): FavoritesSyncService

    @Binds
    @Singleton
    fun bookmarksSyncService(impl: BookmarksSyncServiceImpl): BookmarksSyncService
}

package me.rutrackersearch.app.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.sync.BookmarksSyncServiceImpl
import me.rutrackersearch.data.sync.FavoritesSyncServiceImpl
import me.rutrackersearch.data.sync.LoadFavoritesServiceImpl
import me.rutrackersearch.domain.service.BookmarksSyncService
import me.rutrackersearch.domain.service.FavoritesSyncService
import me.rutrackersearch.domain.service.LoadFavoritesService
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

    @Binds
    @Singleton
    fun loadFavoritesService(impl: LoadFavoritesServiceImpl): LoadFavoritesService

    companion object {
        @Provides
        @Singleton
        fun workManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)
    }
}

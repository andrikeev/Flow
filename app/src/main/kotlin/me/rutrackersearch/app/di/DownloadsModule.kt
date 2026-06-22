package me.rutrackersearch.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import flow.downloads.api.DownloadService
import flow.downloads.api.createDownloadService
import javax.inject.Singleton

/**
 * Bridges the framework-agnostic [DownloadService] into the Android Hilt graph.
 * core:downloads no longer depends on Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object DownloadsModule {
    @Provides
    @Singleton
    fun downloadService(
        @ApplicationContext context: Context,
    ): DownloadService = createDownloadService(context)
}

package flow.downloads.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.downloads.api.DownloadService
import flow.downloads.impl.DownloadServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DownloadsModule {

    @Binds
    @Singleton
    fun downloadService(impl: DownloadServiceImpl): DownloadService
}

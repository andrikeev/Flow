package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.notification.NotificationService
import me.rutrackersearch.notification.NotificationServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApplicationModule {
    @Binds
    @Singleton
    fun notificationService(impl: NotificationServiceImpl): NotificationService
}

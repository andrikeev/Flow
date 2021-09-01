package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.app.notifications.NotificationServiceImpl
import me.rutrackersearch.data.notifications.NotificationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApplicationBindModule {
    @Binds
    @Singleton
    fun notificationService(impl: NotificationServiceImpl): NotificationService
}

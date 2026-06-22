package me.rutrackersearch.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import flow.notifications.NotificationService
import flow.notifications.createNotificationService
import javax.inject.Singleton

/**
 * Bridges the framework-agnostic [NotificationService] into the Android Hilt graph.
 * core:notifications no longer depends on Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    @Provides
    @Singleton
    fun notificationService(
        @ApplicationContext context: Context,
    ): NotificationService = createNotificationService(context)
}

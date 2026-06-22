package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.notifications.NotificationService
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the single [NotificationService] (notificationsModule);
 * remaining Hilt consumers read it from Koin during the migration. Removed in Ф7.
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    @Provides
    @Singleton
    fun notificationService(): NotificationService = GlobalContext.get().get()
}

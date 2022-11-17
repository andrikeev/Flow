package flow.notifications.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import flow.notifications.NotificationService
import flow.notifications.NotificationServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    @Provides
    @Singleton
    fun provideNotificationService(
        @ApplicationContext context: Context,
    ): NotificationService = NotificationServiceImpl(context)
}

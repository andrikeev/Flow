package me.rutrackersearch.app.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    companion object {
        @Provides
        @Singleton
        fun provideAppContext(@ApplicationContext context: Context): Context = context
    }
}

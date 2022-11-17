package flow.securestorage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.securestorage.SecurePreferencesStorage
import flow.securestorage.SecureStorage
import flow.securestorage.preferences.SecurePreferencesFactory
import flow.securestorage.preferences.SecurePreferencesFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SecureStorageModule {

    @Binds
    @Singleton
    fun secureStorage(impl: SecurePreferencesStorage): SecureStorage

    @Binds
    @Singleton
    fun securePreferencesFactory(impl: SecurePreferencesFactoryImpl): SecurePreferencesFactory
}

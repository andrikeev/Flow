package flow.securestorage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.securestorage.PreferencesStorage
import flow.securestorage.PreferencesStorageImpl
import flow.securestorage.preferences.SharedPreferencesFactory
import flow.securestorage.preferences.SharedPreferencesFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SecureStorageModule {

    @Binds
    @Singleton
    fun secureStorage(impl: PreferencesStorageImpl): PreferencesStorage

    @Binds
    @Singleton
    fun securePreferencesFactory(impl: SharedPreferencesFactoryImpl): SharedPreferencesFactory
}

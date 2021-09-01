package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.security.SecureStorageFactory
import me.rutrackersearch.data.security.SecureStorageFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SecurityModule {
    @Binds
    @Singleton
    fun secureStorageFactory(impl: SecureStorageFactoryImpl): SecureStorageFactory
}

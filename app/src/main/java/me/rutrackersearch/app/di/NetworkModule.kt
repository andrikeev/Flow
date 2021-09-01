package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.network.ServerApi
import me.rutrackersearch.data.network.ServerApiFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideServerApi(factory: ServerApiFactory): ServerApi {
        return factory.get()
    }
}

package flow.logger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.logger.api.LoggerFactory
import flow.logger.impl.LoggerFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface LoggerModule {
    @Binds
    @Singleton
    fun loggerFactory(impl: LoggerFactoryImpl): LoggerFactory
}

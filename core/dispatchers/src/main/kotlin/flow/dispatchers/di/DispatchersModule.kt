package flow.dispatchers.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.dispatchers.api.Dispatchers
import flow.dispatchers.impl.DispatchersImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DispatchersModule {
    @Binds
    @Singleton
    fun dispatchers(impl: DispatchersImpl): Dispatchers
}

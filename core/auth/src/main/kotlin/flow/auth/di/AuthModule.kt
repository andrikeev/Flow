package flow.auth.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.auth.api.AuthRepository
import flow.auth.api.AuthService
import flow.auth.impl.AuthRepositoryImpl
import flow.auth.impl.AuthServiceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthModule {
    @Binds
    @Singleton
    fun authRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun authService(impl: AuthServiceImpl): AuthService

    companion object {

        @Provides
        @Singleton
        @Named("auth")
        fun okHttpClient(): OkHttpClient {
            return OkHttpClient.Builder().apply {
                addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            }.build()
        }
    }
}

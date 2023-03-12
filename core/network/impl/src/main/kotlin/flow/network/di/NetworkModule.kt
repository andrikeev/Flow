package flow.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.network.api.NetworkApi
import flow.network.api.RuTrackerApiFactory
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun networkApi(httpClient: HttpClient): NetworkApi {
        return RuTrackerApiFactory.create(httpClient)
    }

    @Provides
    @Singleton
    fun httpClient(): HttpClient {
        return HttpClient(OkHttp) {
            defaultRequest { url("https://rutracker.org/forum/") }
            install(Logging)
        }
    }
}

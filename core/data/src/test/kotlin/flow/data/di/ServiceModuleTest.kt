package flow.data.di

import android.content.Context
import flow.auth.api.AuthService
import flow.auth.api.TokenProvider
import flow.dispatchers.api.Dispatchers
import flow.logger.api.LoggerFactory
import flow.network.api.NetworkApi
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class ServiceModuleTest {

    @Test
    fun serviceGraphIsComplete() {
        serviceModule.verify(
            extraTypes = listOf(
                Context::class,
                Dispatchers::class,
                LoggerFactory::class,
                NetworkApi::class,
                AuthService::class,
                TokenProvider::class,
            ),
        )
    }
}

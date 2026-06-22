package flow.network.di

import flow.data.api.repository.SettingsRepository
import flow.dispatchers.api.Dispatchers
import flow.logger.api.LoggerFactory
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class NetworkModuleTest {

    @Test
    fun networkGraphIsComplete() {
        networkModule.verify(
            extraTypes = listOf(
                SettingsRepository::class,
                Dispatchers::class,
                LoggerFactory::class,
            ),
        )
    }
}

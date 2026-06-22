package flow.network.di

import flow.data.api.repository.SettingsRepository
import flow.dispatchers.api.Dispatchers
import flow.logger.api.LoggerFactory
import flow.network.impl.ProxyControllerImpl
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
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
            // ProxyControllerImpl receives a lazy () -> OkHttpClient supplied directly in
            // the module definition (breaks the OkHttpClient <-> ProxyController cycle), so
            // it is not a Koin-resolved dependency.
            injections = injectedParameters(
                definition<ProxyControllerImpl>(Function0::class),
            ),
        )
    }
}

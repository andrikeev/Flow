package flow.data.di

import android.content.Context
import flow.dispatchers.api.Dispatchers
import flow.logger.api.LoggerFactory
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
            ),
        )
    }
}

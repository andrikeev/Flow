package me.rutrackersearch.app.di

import flow.dispatchers.api.Dispatchers
import flow.dispatchers.di.dispatchersModule
import flow.logger.api.LoggerFactory
import flow.logger.di.loggerModule
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.dsl.koinApplication

/**
 * Smoke test for the Koin module graph. Grows as modules migrate off Hilt; once Koin
 * Annotations land it will use verify()/checkModules to validate the whole graph at
 * test time (the main safety net for the runtime DI, since the app is not launched in CI).
 *
 * For now it asserts the dependency-free infra modules resolve.
 */
class KoinModulesSmokeTest {
    @Test
    fun infraModulesResolve() {
        val koin =
            koinApplication {
                modules(dispatchersModule, loggerModule)
            }.koin

        assertNotNull(koin.get<Dispatchers>())
        assertNotNull(koin.get<LoggerFactory>())

        koin.close()
    }
}

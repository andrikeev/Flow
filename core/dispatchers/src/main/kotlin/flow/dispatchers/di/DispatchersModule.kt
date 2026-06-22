package flow.dispatchers.di

import flow.dispatchers.api.Dispatchers
import flow.dispatchers.api.createDispatchers
import org.koin.dsl.module

/**
 * Koin module for [Dispatchers] — the target DI wiring for the multiplatform graph.
 * On Android the same binding is bridged into Hilt until the app is migrated to Koin.
 */
val dispatchersModule = module {
    single<Dispatchers> { createDispatchers() }
}

package flow.logger.di

import flow.logger.api.LoggerFactory
import flow.logger.api.createLoggerFactory
import org.koin.dsl.module

/**
 * Koin module for [LoggerFactory] — the target DI wiring for the multiplatform graph.
 * On Android the same binding is bridged into Hilt until the app is migrated to Koin.
 */
val loggerModule = module {
    single<LoggerFactory> { createLoggerFactory() }
}

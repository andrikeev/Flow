package flow.logger.api

import flow.logger.impl.LoggerFactoryImpl

/**
 * Framework-agnostic factory for [LoggerFactory]. Resolves to the build-variant
 * specific implementation (debug logs via Logcat, release is a no-op stub) and is
 * used by both the Koin module and the Hilt bridge in the Android app.
 */
fun createLoggerFactory(): LoggerFactory = LoggerFactoryImpl()

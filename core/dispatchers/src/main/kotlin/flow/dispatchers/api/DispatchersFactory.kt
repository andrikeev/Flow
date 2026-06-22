package flow.dispatchers.api

import flow.dispatchers.impl.DispatchersImpl

/**
 * Framework-agnostic factory for [Dispatchers]. Used by the Koin module and by the
 * Hilt bridge in the Android app, so the implementation stays free of any DI framework.
 */
fun createDispatchers(): Dispatchers = DispatchersImpl()

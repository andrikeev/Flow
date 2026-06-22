package flow.data.di

import flow.data.api.service.ConnectionService
import flow.data.api.service.StoreService
import flow.data.impl.service.ConnectionServiceImpl
import flow.data.impl.service.StoreServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the Context-only data services. Network-backed services stay on Hilt
 * (DataModule) until they migrate with network:impl + auth. Dependencies (Context,
 * Dispatchers, LoggerFactory) come from the Koin graph; validated by ServiceModuleTest.
 */
val serviceModule = module {
    singleOf(::ConnectionServiceImpl) bind ConnectionService::class
    singleOf(::StoreServiceImpl) bind StoreService::class
}

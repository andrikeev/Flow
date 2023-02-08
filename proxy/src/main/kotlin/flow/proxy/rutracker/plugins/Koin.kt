package flow.proxy.rutracker.plugins

import flow.proxy.rutracker.di.appModule
import io.ktor.events.EventDefinition
import io.ktor.server.application.*
import io.ktor.util.*
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

internal fun Application.configureKoin() {
    koin {
        modules(appModule)
    }
}

private val koinApplicationStarted = EventDefinition<KoinApplication>()
private val koinApplicationStopPreparing = EventDefinition<KoinApplication>()
private val koinApplicationStopped = EventDefinition<KoinApplication>()

private class Koin(val koinApplication: KoinApplication) {

    companion object Feature : BaseApplicationPlugin<Application, KoinApplication, Koin> {
        override val key = AttributeKey<Koin>("Koin")

        override fun install(pipeline: Application, configure: KoinAppDeclaration): Koin {
            val monitor = pipeline.environment.monitor

            val koinApplication = startKoin(appDeclaration = configure)
            monitor.raise(koinApplicationStarted, koinApplication)

            monitor.subscribe(ApplicationStopping) {
                monitor.raise(koinApplicationStopPreparing, koinApplication)
                stopKoin()
                monitor.raise(koinApplicationStopped, koinApplication)
            }

            return Koin(koinApplication)
        }
    }
}

@KtorDsl
private fun Application.koin(configuration: KoinAppDeclaration) = pluginOrNull(Koin)?.apply {
    koinApplication.apply(configuration).createEagerInstances()
} ?: install(Koin, configuration)

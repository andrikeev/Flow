package flow.proxy.rutracker.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import org.slf4j.event.Level

internal fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.ERROR
    }
}

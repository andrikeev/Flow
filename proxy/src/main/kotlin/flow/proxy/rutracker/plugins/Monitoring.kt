package flow.proxy.rutracker.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

internal fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}

package flow.proxy.rutracker

import flow.proxy.rutracker.plugins.configureKoin
import flow.proxy.rutracker.plugins.configureMonitoring
import flow.proxy.rutracker.plugins.configureSerialization
import flow.proxy.rutracker.plugins.configureStatusPages
import flow.proxy.rutracker.routes.configureAuthRoutes
import flow.proxy.rutracker.routes.configureFavoritesRoutes
import flow.proxy.rutracker.routes.configureForumRoutes
import flow.proxy.rutracker.routes.configureMainRoutes
import flow.proxy.rutracker.routes.configureSearchRoutes
import flow.proxy.rutracker.routes.configureStaticRoutes
import flow.proxy.rutracker.routes.configureTopicRoutes
import flow.proxy.rutracker.routes.configureTorrentRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureKoin()
        configureMonitoring()
        configureSerialization()
        configureStatusPages()
        configureMainRoutes()
        configureAuthRoutes()
        configureForumRoutes()
        configureSearchRoutes()
        configureTopicRoutes()
        configureTorrentRoutes()
        configureFavoritesRoutes()
        configureStaticRoutes()
    }.start(wait = true)
}

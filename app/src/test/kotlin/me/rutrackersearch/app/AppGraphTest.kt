package me.rutrackersearch.app

import android.content.Context
import androidx.work.WorkerParameters
import flow.account.di.accountModule
import flow.auth.di.authModule
import flow.connection.di.connectionModule
import flow.data.di.repositoryModule
import flow.data.di.serviceModule
import flow.database.di.databaseModule
import flow.dispatchers.di.dispatchersModule
import flow.domain.di.domainModule
import flow.downloads.di.downloadsModule
import flow.favorites.di.favoritesModule
import flow.forum.bookmarks.di.bookmarksModule
import flow.forum.category.di.categoryModule
import flow.forum.di.forumModule
import flow.logger.di.loggerModule
import flow.login.di.loginModule
import flow.main.di.mainModule
import flow.menu.di.menuModule
import flow.models.search.Filter
import flow.network.di.networkDebugModules
import flow.network.di.networkModule
import flow.notifications.di.notificationsModule
import flow.rating.di.ratingModule
import flow.search.di.searchModule
import flow.search.input.di.searchInputModule
import flow.search.result.di.searchResultModule
import flow.securestorage.di.preferencesModule
import flow.topic.di.topicModule
import flow.visited.di.visitedModule
import flow.work.di.workModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

/**
 * Statically verifies the complete application dependency graph — the same set of modules
 * that [FlowApplication] assembles at startup. Unlike the per-module verify tests, this
 * catches missing bindings that only surface across module boundaries (e.g. a use case in
 * core:domain depending on a repository in core:data).
 *
 * Whitelisted types are dependencies that Koin supplies at runtime rather than from a module:
 * - [Context] / [WorkerParameters] are provided by androidContext() / the KoinWorkerFactory.
 * - [Filter] is an assisted parameter passed to the search ViewModels via parametersOf().
 * - [Function0] is the lazy `() -> OkHttpClient` handed to ProxyControllerImpl to break the
 *   OkHttpClient <-> ProxyController cycle.
 */
@OptIn(KoinExperimentalAPI::class)
class AppGraphTest {
    @Test
    fun appGraphIsComplete() {
        // verify() flattens included modules into a single graph, so the whole graph is
        // checked together — cross-module bindings included. (verifyAll() instead checks each
        // module in isolation, which would flag every cross-module dependency as missing.)
        val appGraph =
            module {
                includes(
                    // core
                    databaseModule,
                    dispatchersModule,
                    loggerModule,
                    preferencesModule,
                    notificationsModule,
                    downloadsModule,
                    repositoryModule,
                    serviceModule,
                    networkModule,
                    authModule,
                    domainModule,
                    workModule,
                    // features
                    accountModule,
                    bookmarksModule,
                    connectionModule,
                    categoryModule,
                    favoritesModule,
                    forumModule,
                    loginModule,
                    mainModule,
                    menuModule,
                    ratingModule,
                    searchModule,
                    searchInputModule,
                    searchResultModule,
                    topicModule,
                    visitedModule,
                    *networkDebugModules().toTypedArray(),
                )
            }

        appGraph.verify(
            extraTypes =
                listOf(
                    Context::class,
                    WorkerParameters::class,
                    Filter::class,
                    Function0::class,
                ),
        )
    }
}

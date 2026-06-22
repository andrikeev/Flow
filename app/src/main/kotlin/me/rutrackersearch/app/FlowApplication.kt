package me.rutrackersearch.app

import android.app.Application
import android.os.StrictMode
import androidx.work.Configuration
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
import flow.login.di.loginModule
import flow.logger.di.loggerModule
import flow.main.di.mainModule
import flow.menu.di.menuModule
import flow.network.api.ImageLoader
import flow.network.api.ProxyController
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
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class FlowApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()

    override fun onCreate() {
        // Koin must be started before WorkManager initializes (on demand) and before any
        // platform setup below, so it is the first thing in onCreate.
        startKoin {
            androidContext(this@FlowApplication)
            modules(
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
            )
            modules(networkDebugModules())
        }
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )
        }
        super.onCreate()
        val koin = GlobalContext.get()
        koin.get<ImageLoader>().setup()
        koin.get<ProxyController>().setup()
    }
}

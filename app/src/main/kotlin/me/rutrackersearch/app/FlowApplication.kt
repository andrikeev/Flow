package me.rutrackersearch.app

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import flow.database.di.databaseModule
import flow.dispatchers.di.dispatchersModule
import flow.downloads.di.downloadsModule
import flow.logger.di.loggerModule
import flow.network.api.ImageLoader
import flow.network.api.ProxyController
import flow.notifications.di.notificationsModule
import flow.securestorage.di.preferencesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import javax.inject.Inject

@HiltAndroidApp
class FlowApplication : Application() {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var proxyController: ProxyController

    override fun onCreate() {
        // Koin is bootstrapped before Hilt (super.onCreate) so Hilt inverse-bridges can
        // resolve Koin-owned instances. During the migration Koin and Hilt coexist; more
        // modules join the Koin graph as they are migrated off Hilt.
        startKoin {
            androidContext(this@FlowApplication)
            modules(
                databaseModule,
                dispatchersModule,
                loggerModule,
                preferencesModule,
                notificationsModule,
                downloadsModule,
            )
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
        imageLoader.setup()
        proxyController.setup()
    }
}

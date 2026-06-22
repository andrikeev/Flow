package me.rutrackersearch.app

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import flow.network.api.ImageLoader
import flow.network.api.ProxyController
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
        // Koin is bootstrapped before Hilt (super.onCreate) so that, as modules are
        // migrated, Hilt inverse-bridges can resolve Koin-owned instances. During the
        // migration Koin and Hilt coexist; the module graph is empty until Ф1.
        startKoin {
            androidContext(this@FlowApplication)
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

package me.rutrackersearch.app

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import flow.network.api.ImageLoader
import javax.inject.Inject

@HiltAndroidApp
class FlowApplication : Application() {
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate() {
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
    }
}

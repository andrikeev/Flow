package flow.downloads.di

import flow.downloads.api.DownloadService
import flow.downloads.api.createDownloadService
import org.koin.dsl.module

/**
 * Koin module for [DownloadService]. Context is resolved from the Koin graph
 * (androidContext()). Exposed to remaining Hilt consumers via an inverse-bridge in :app.
 */
val downloadsModule = module {
    single<DownloadService> { createDownloadService(get()) }
}

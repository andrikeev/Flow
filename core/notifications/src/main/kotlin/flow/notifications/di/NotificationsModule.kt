package flow.notifications.di

import flow.notifications.NotificationService
import flow.notifications.createNotificationService
import org.koin.dsl.module

/**
 * Koin module for [NotificationService]. Context is resolved from the Koin graph
 * (androidContext()). Exposed to remaining Hilt consumers via an inverse-bridge in :app.
 */
val notificationsModule = module {
    single<NotificationService> { createNotificationService(get()) }
}

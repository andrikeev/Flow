package flow.auth.di

import flow.auth.api.AuthService
import flow.auth.api.TokenProvider
import flow.auth.api.createAuthService
import org.koin.dsl.module

/**
 * Koin module for the auth service — the target DI wiring for the multiplatform graph.
 * One stateful instance backs both [AuthService] and [TokenProvider]. On Android the
 * same binding is bridged into Hilt until the app is migrated to Koin.
 */
val authModule = module {
    single<AuthService> { createAuthService(get(), get()) }
    single<TokenProvider> { get<AuthService>() as TokenProvider }
}

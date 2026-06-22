package flow.network.di

import org.koin.core.module.Module

/** Release builds add no network debug modules. */
fun networkDebugModules(): List<Module> = emptyList()

package flow.connection.di

import flow.connection.ConnectionViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val connectionModule = module {
    viewModelOf(::ConnectionViewModel)
}

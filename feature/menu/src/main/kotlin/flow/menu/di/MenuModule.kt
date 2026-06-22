package flow.menu.di

import flow.menu.MenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val menuModule = module {
    viewModelOf(::MenuViewModel)
}

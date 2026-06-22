package flow.account.di

import flow.account.AccountViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val accountModule = module {
    viewModelOf(::AccountViewModel)
}

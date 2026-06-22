package flow.search.input.di

import flow.search.input.SearchInputViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val searchInputModule = module {
    viewModelOf(::SearchInputViewModel)
}

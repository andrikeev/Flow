package flow.forum.category.di

import flow.forum.category.CategoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val categoryModule = module {
    viewModelOf(::CategoryViewModel)
}

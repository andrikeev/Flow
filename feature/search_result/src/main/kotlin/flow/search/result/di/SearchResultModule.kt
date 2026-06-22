package flow.search.result.di

import flow.search.result.SearchResultViewModel
import flow.search.result.categories.CategorySelectionViewModel
import flow.search.result.domain.GetCategoriesByGroupIdUseCase
import flow.search.result.domain.GetFlattenForumTreeUseCase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val searchResultModule = module {
    factoryOf(::GetCategoriesByGroupIdUseCase)
    factoryOf(::GetFlattenForumTreeUseCase)
    viewModelOf(::SearchResultViewModel)
    viewModelOf(::CategorySelectionViewModel)
}

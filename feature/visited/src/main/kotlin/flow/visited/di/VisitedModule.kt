package flow.visited.di

import flow.visited.VisitedViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val visitedModule = module {
    viewModelOf(::VisitedViewModel)
}

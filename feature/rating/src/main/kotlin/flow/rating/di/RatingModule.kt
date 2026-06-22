package flow.rating.di

import flow.rating.RatingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val ratingModule = module {
    viewModelOf(::RatingViewModel)
}

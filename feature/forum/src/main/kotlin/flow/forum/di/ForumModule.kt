package flow.forum.di

import flow.forum.ForumViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val forumModule = module {
    viewModelOf(::ForumViewModel)
}

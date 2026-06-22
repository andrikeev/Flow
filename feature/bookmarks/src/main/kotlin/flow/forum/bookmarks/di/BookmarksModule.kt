package flow.forum.bookmarks.di

import flow.forum.bookmarks.BookmarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val bookmarksModule = module {
    viewModelOf(::BookmarksViewModel)
}

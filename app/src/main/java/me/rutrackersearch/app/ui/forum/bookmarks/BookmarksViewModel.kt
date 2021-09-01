package me.rutrackersearch.app.ui.forum.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import me.rutrackersearch.domain.usecase.ObserveBookmarksUseCase
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    observeBookmarksUseCase: ObserveBookmarksUseCase,
) : ViewModel() {
    val state: StateFlow<BookmarksState> = observeBookmarksUseCase()
        .mapLatest { items ->
            if (items.isEmpty()) {
                BookmarksState.Empty
            } else {
                BookmarksState.BookmarksList(items)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, BookmarksState.Initial)
}

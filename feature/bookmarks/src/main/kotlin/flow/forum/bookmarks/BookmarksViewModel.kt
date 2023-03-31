package flow.forum.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveBookmarksUseCase
import flow.logger.api.LoggerFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class BookmarksViewModel @Inject constructor(
    private val observeBookmarksUseCase: ObserveBookmarksUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<BookmarksState, BookmarksSideEffect> {
    private val logger = loggerFactory.get("BookmarksViewModel")

    override val container: Container<BookmarksState, BookmarksSideEffect> = container(
        initialState = BookmarksState.Initial,
        onCreate = { observeBookmarks() },
    )

    fun perform(action: BookmarksAction) {
        logger.d { "Perform $action" }
        when (action) {
            is BookmarksAction.BookmarkClicked -> intent {
                postSideEffect(BookmarksSideEffect.OpenCategory(action.bookmark.category.id))
            }
        }
    }

    private fun observeBookmarks() = viewModelScope.launch {
        logger.d { "Start observing bookmarks" }
        observeBookmarksUseCase()
            .catch { emit(emptyList()) }
            .mapLatest { bookmarks ->
                if (bookmarks.isEmpty()) {
                    BookmarksState.Empty
                } else {
                    BookmarksState.BookmarksList(bookmarks)
                }
            }.collectLatest { state -> intent { reduce { state } } }
    }
}

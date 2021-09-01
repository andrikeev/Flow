package me.rutrackersearch.app.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.rutrackersearch.domain.usecase.ClearBookmarksUseCase
import me.rutrackersearch.domain.usecase.ClearFavoritesUseCase
import me.rutrackersearch.domain.usecase.ClearHistoryUseCase
import me.rutrackersearch.domain.usecase.ObserveSettingsUseCase
import me.rutrackersearch.domain.usecase.SetBookmarksSyncPeriodUseCase
import me.rutrackersearch.domain.usecase.SetFavoritesSyncPeriodUseCase
import me.rutrackersearch.domain.usecase.SetThemeUseCase
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val clearBookmarksUseCase: ClearBookmarksUseCase,
    private val clearFavoritesUseCase: ClearFavoritesUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setFavoritesSyncPeriodUseCase: SetFavoritesSyncPeriodUseCase,
    private val setBookmarksSyncPeriodUseCase: SetBookmarksSyncPeriodUseCase,
) : ViewModel() {
    val state: Flow<MenuState> = observeSettingsUseCase()
        .map { settings ->
            MenuState(
                theme = settings.theme,
                favoritesSyncPeriod = settings.favoritesSyncPeriod,
                bookmarksSyncPeriod = settings.bookmarksSyncPeriod,
            )
        }

    fun perform(action: MenuAction) {
        viewModelScope.launch {
            when (action) {
                is MenuAction.ClearHistoryClick -> clearHistoryUseCase()
                is MenuAction.ClearFavoritesClick -> clearFavoritesUseCase()
                is MenuAction.ClearBookmarksClick -> clearBookmarksUseCase()
                is MenuAction.SetTheme -> setThemeUseCase(action.theme)
                is MenuAction.SetFavoritesSyncPeriod -> {
                    setFavoritesSyncPeriodUseCase(action.syncPeriod)
                }
                is MenuAction.SetBookmarksSyncPeriod -> {
                    setBookmarksSyncPeriodUseCase(action.syncPeriod)
                }
                else -> Unit
            }
        }
    }
}

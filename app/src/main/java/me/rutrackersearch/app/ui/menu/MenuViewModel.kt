package me.rutrackersearch.app.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.rutrackersearch.domain.usecase.ClearBookmarksUseCase
import me.rutrackersearch.domain.usecase.ClearFavoritesUseCase
import me.rutrackersearch.domain.usecase.ClearHistoryUseCase
import me.rutrackersearch.domain.usecase.ObserveSettingsUseCase
import me.rutrackersearch.domain.usecase.SetBookmarksSyncPeriodUseCase
import me.rutrackersearch.domain.usecase.SetFavoritesSyncPeriodUseCase
import me.rutrackersearch.domain.usecase.SetThemeUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val clearBookmarksUseCase: ClearBookmarksUseCase,
    private val clearFavoritesUseCase: ClearFavoritesUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val setBookmarksSyncPeriodUseCase: SetBookmarksSyncPeriodUseCase,
    private val setFavoritesSyncPeriodUseCase: SetFavoritesSyncPeriodUseCase,
    private val setThemeUseCase: SetThemeUseCase,
) : ViewModel(), ContainerHost<MenuState, MenuSideEffect> {
    override val container: Container<MenuState, MenuSideEffect> = container(
        initialState = MenuState(),
        onCreate = { observeSettings() }
    )

    fun perform(action: MenuAction) = intent {
        when (action) {
            is MenuAction.ClearHistoryClick -> clearHistoryUseCase()
            is MenuAction.ClearFavoritesClick -> clearFavoritesUseCase()
            is MenuAction.ClearBookmarksClick -> clearBookmarksUseCase()
            is MenuAction.PrivacyPolicyClick -> postSideEffect(MenuSideEffect.OpenLink(privacyPolicyURL))
            is MenuAction.RightsClick -> postSideEffect(MenuSideEffect.OpenLink(informationForRightOwnersURL))
            is MenuAction.SendFeedbackClick -> postSideEffect(MenuSideEffect.OpenLink(developerEmailURI))
            is MenuAction.SetTheme -> setThemeUseCase(action.theme)
            is MenuAction.SetFavoritesSyncPeriod -> setFavoritesSyncPeriodUseCase(action.syncPeriod)
            is MenuAction.SetBookmarksSyncPeriod -> setBookmarksSyncPeriodUseCase(action.syncPeriod)
            is MenuAction.LoginClick -> postSideEffect(MenuSideEffect.OpenLogin)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase().collectLatest { settings ->
                intent {
                    reduce {
                        MenuState(
                            theme = settings.theme,
                            favoritesSyncPeriod = settings.favoritesSyncPeriod,
                            bookmarksSyncPeriod = settings.bookmarksSyncPeriod,
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val developerEmailURI = "mailto:rutracker.search@gmail.com"
        private const val informationForRightOwnersURL = "http://flow.rutrackersearch.me/rights"
        private const val privacyPolicyURL = "http://flow.rutrackersearch.me/privacy-policy"
    }
}

package flow.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ClearBookmarksUseCase
import flow.domain.usecase.ClearFavoritesUseCase
import flow.domain.usecase.ClearHistoryUseCase
import flow.domain.usecase.ObserveSettingsUseCase
import flow.domain.usecase.SetBookmarksSyncPeriodUseCase
import flow.domain.usecase.SetFavoritesSyncPeriodUseCase
import flow.domain.usecase.SetThemeUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class MenuViewModel @Inject constructor(
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
            is MenuAction.AboutClick -> postSideEffect(MenuSideEffect.ShowAbout)
            is MenuAction.ConfirmableAction -> postSideEffect(action.toConfirmation())
            is MenuAction.ClearBookmarksConfirmation -> clearBookmarksUseCase()
            is MenuAction.ClearFavoritesConfirmation -> clearFavoritesUseCase()
            is MenuAction.ClearHistoryConfirmation -> clearHistoryUseCase()
            is MenuAction.LoginClick -> postSideEffect(MenuSideEffect.OpenLogin)
            is MenuAction.PrivacyPolicyClick -> postSideEffect(MenuSideEffect.OpenLink(PrivacyPolicyURL))
            is MenuAction.RightsClick -> postSideEffect(MenuSideEffect.OpenLink(InformationForRightOwnersURL))
            is MenuAction.SendFeedbackClick -> postSideEffect(MenuSideEffect.OpenLink(DeveloperEmailURI))
            is MenuAction.SetBookmarksSyncPeriod -> setBookmarksSyncPeriodUseCase(action.syncPeriod)
            is MenuAction.SetFavoritesSyncPeriod -> setFavoritesSyncPeriodUseCase(action.syncPeriod)
            is MenuAction.SetTheme -> setThemeUseCase(action.theme)
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

    private fun MenuAction.ConfirmableAction.toConfirmation(): MenuSideEffect.ShowConfirmation =
        MenuSideEffect.ShowConfirmation(confirmationMessage, onConfirmAction)

    companion object {
        private const val DeveloperEmailURI = "mailto:rutracker.search@gmail.com"
        private const val InformationForRightOwnersURL = "http://flow.rutrackersearch.me/rights"
        private const val PrivacyPolicyURL = "http://flow.rutrackersearch.me/privacy-policy"
    }
}

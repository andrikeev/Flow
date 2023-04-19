package flow.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ClearBookmarksUseCase
import flow.domain.usecase.ClearHistoryUseCase
import flow.domain.usecase.ClearLocalFavoritesUseCase
import flow.domain.usecase.ObserveSettingsUseCase
import flow.domain.usecase.SetBookmarksSyncPeriodUseCase
import flow.domain.usecase.SetEndpointUseCase
import flow.domain.usecase.SetFavoritesSyncPeriodUseCase
import flow.domain.usecase.SetThemeUseCase
import flow.logger.api.LoggerFactory
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
    private val clearLocalFavoritesUseCase: ClearLocalFavoritesUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val setBookmarksSyncPeriodUseCase: SetBookmarksSyncPeriodUseCase,
    private val setEndpointUseCase: SetEndpointUseCase,
    private val setFavoritesSyncPeriodUseCase: SetFavoritesSyncPeriodUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<MenuState, MenuSideEffect> {
    private val logger = loggerFactory.get("MenuViewModel")

    override val container: Container<MenuState, MenuSideEffect> = container(
        initialState = MenuState(),
        onCreate = { observeSettings() }
    )

    fun perform(action: MenuAction) {
        logger.d { "Perform $action" }
        when (action) {
            is MenuAction.AboutClick -> intent { postSideEffect(MenuSideEffect.ShowAbout) }
            is MenuAction.ConfirmableAction -> intent { postSideEffect(action.toConfirmation()) }
            is MenuAction.ClearBookmarksConfirmation -> viewModelScope.launch { clearBookmarksUseCase() }
            is MenuAction.ClearFavoritesConfirmation -> viewModelScope.launch { clearLocalFavoritesUseCase() }
            is MenuAction.ClearHistoryConfirmation -> viewModelScope.launch { clearHistoryUseCase() }
            is MenuAction.LoginClick -> intent { postSideEffect(MenuSideEffect.OpenLogin) }
            is MenuAction.PrivacyPolicyClick -> intent { postSideEffect(MenuSideEffect.OpenLink(PrivacyPolicy)) }
            is MenuAction.RightsClick -> intent { postSideEffect(MenuSideEffect.OpenLink(Copyrights)) }
            is MenuAction.SendFeedbackClick -> intent { postSideEffect(MenuSideEffect.OpenLink(DeveloperEmail)) }
            is MenuAction.SetBookmarksSyncPeriod -> viewModelScope.launch { setBookmarksSyncPeriodUseCase(action.syncPeriod) }
            is MenuAction.SetEndpoint -> viewModelScope.launch { setEndpointUseCase(action.endpoint) }
            is MenuAction.SetFavoritesSyncPeriod -> viewModelScope.launch { setFavoritesSyncPeriodUseCase(action.syncPeriod) }
            is MenuAction.SetTheme -> viewModelScope.launch { setThemeUseCase(action.theme) }
        }
    }

    private fun observeSettings() {
        logger.d { "Start observing settings" }
        viewModelScope.launch {
            observeSettingsUseCase().collectLatest { settings ->
                intent {
                    reduce {
                        MenuState(
                            theme = settings.theme,
                            endpoint = settings.endpoint,
                            favoritesSyncPeriod = settings.favoritesSyncPeriod,
                            bookmarksSyncPeriod = settings.bookmarksSyncPeriod,
                        )
                    }
                }
            }
        }
    }

    private fun MenuAction.ConfirmableAction.toConfirmation() =
        MenuSideEffect.ShowConfirmation(title, confirmationMessage, onConfirmAction)

    companion object {
        private const val DeveloperEmail = "mailto:rutracker.search@gmail.com"
        private const val Copyrights = "https://flow-proxy-m7o3b.ondigitalocean.app/copyrights.html"
        private const val PrivacyPolicy = "https://flow-proxy-m7o3b.ondigitalocean.app/privacy-policy.html"
    }
}

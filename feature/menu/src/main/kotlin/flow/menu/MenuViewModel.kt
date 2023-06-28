package flow.menu

import androidx.lifecycle.ViewModel
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
import flow.models.settings.Endpoint
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import kotlinx.coroutines.flow.collectLatest
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
            is MenuAction.AboutClick -> onAboutClick()
            is MenuAction.ClearBookmarksConfirmation -> onClearBookmarksConfirmation()
            is MenuAction.ClearFavoritesConfirmation -> onClearFavoritesConfirmation()
            is MenuAction.ClearHistoryConfirmation -> onClearHistoryConfirmation()
            is MenuAction.ConfirmableAction -> onConfirmableAction(action)
            is MenuAction.LoginClick -> onLoginClick()
            is MenuAction.MyTipsClick -> onMyTipsClick()
            is MenuAction.NetMonetClick -> onNetMonetClick()
            is MenuAction.PayPalClick -> onPayPalClick()
            is MenuAction.PrivacyPolicyClick -> onPrivacyPolicyClick()
            is MenuAction.RightsClick -> onRightsClick()
            is MenuAction.SendFeedbackClick -> onSendFeedbackClick()
            is MenuAction.SetBookmarksSyncPeriod -> onSetBookmarksSyncPeriod(action.syncPeriod)
            is MenuAction.SetEndpoint -> onSetEndpoint(action.endpoint)
            is MenuAction.SetFavoritesSyncPeriod -> onSetFavoritesSyncPeriod(action.syncPeriod)
            is MenuAction.SetTheme -> onSetTheme(action.theme)
        }
    }

    private fun observeSettings() = intent {
        logger.d { "Start observing settings" }
        observeSettingsUseCase().collectLatest { settings ->
            reduce {
                logger.d { "On new settings: $settings" }
                MenuState(
                    theme = settings.theme,
                    favoritesSyncPeriod = settings.favoritesSyncPeriod,
                    bookmarksSyncPeriod = settings.bookmarksSyncPeriod,
                )
            }
        }
    }

    private fun onAboutClick() = intent {
        postSideEffect(MenuSideEffect.ShowAbout)
    }

    private fun onConfirmableAction(action: MenuAction.ConfirmableAction) = intent {
        postSideEffect(action.toConfirmation())
    }

    private fun onClearBookmarksConfirmation() = intent {
        clearBookmarksUseCase()
    }

    private fun onClearFavoritesConfirmation() = intent {
        clearLocalFavoritesUseCase()
    }

    private fun onClearHistoryConfirmation() = intent {
        clearHistoryUseCase()
    }

    private fun onLoginClick() = intent {
        postSideEffect(MenuSideEffect.OpenLogin)
    }

    private fun onMyTipsClick() = intent {
        postSideEffect(MenuSideEffect.OpenLink(MyTips))
    }

    private fun onNetMonetClick() = intent {
        postSideEffect(MenuSideEffect.OpenLink(NetMonet))
    }

    private fun onPayPalClick() = intent {
        postSideEffect(MenuSideEffect.OpenLink(PayPal))
    }

    private fun onPrivacyPolicyClick() = intent {
        postSideEffect(MenuSideEffect.OpenLink(PrivacyPolicy))
    }

    private fun onRightsClick() = intent {
        postSideEffect(MenuSideEffect.OpenLink(Copyrights))
    }

    private fun onSendFeedbackClick() = intent {
        postSideEffect(MenuSideEffect.OpenLink(DeveloperEmail))
    }

    private fun onSetBookmarksSyncPeriod(period: SyncPeriod) = intent {
        setBookmarksSyncPeriodUseCase(period)
    }

    private fun onSetEndpoint(endpoint: Endpoint) = intent {
        setEndpointUseCase(endpoint)
    }

    private fun onSetFavoritesSyncPeriod(period: SyncPeriod) = intent {
        setFavoritesSyncPeriodUseCase(period)
    }

    private fun onSetTheme(theme: Theme) = intent {
        setThemeUseCase(theme)
    }

    private fun MenuAction.ConfirmableAction.toConfirmation() =
        MenuSideEffect.ShowConfirmation(title, confirmationMessage, onConfirmAction)

    companion object {
        private const val DeveloperEmail = "mailto:rutracker.search@gmail.com"
        private const val Copyrights = "https://flow-proxy-m7o3b.ondigitalocean.app/copyrights.html"
        private const val PrivacyPolicy =
            "https://flow-proxy-m7o3b.ondigitalocean.app/privacy-policy.html"
        private const val PayPal = "https://www.paypal.com/donate/?hosted_button_id=LHVXE7WPCY846"
        private const val NetMonet = "https://netmonet.ru/s/andrikeev"
        private const val MyTips = "https://pay.mysbertips.ru/50962107"
    }
}

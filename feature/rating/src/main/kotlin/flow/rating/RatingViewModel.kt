package flow.rating

import androidx.lifecycle.ViewModel
import flow.domain.model.rating.RatingRequest
import flow.domain.usecase.AppLaunchedUseCase
import flow.domain.usecase.DisableRatingRequestUseCase
import flow.domain.usecase.GetRatingStoreUseCase
import flow.domain.usecase.ObserveRatingRequestUseCase
import flow.domain.usecase.PostponeRatingRequestUseCase
import flow.logger.api.LoggerFactory
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class RatingViewModel(
    private val appLaunchedUseCase: AppLaunchedUseCase,
    private val disableRatingRequestUseCase: DisableRatingRequestUseCase,
    private val getRatingStoreUseCase: GetRatingStoreUseCase,
    private val observeRatingRequestUseCase: ObserveRatingRequestUseCase,
    private val postponeRatingRequestUseCase: PostponeRatingRequestUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<RatingRequest, RatingSideEffect> {
    private val logger = loggerFactory.get("RatingViewModel")

    override val container: Container<RatingRequest, RatingSideEffect> = container(
        initialState = RatingRequest.Hide,
        onCreate = {
            appLaunchedUseCase()
            repeatOnSubscription {
                observeRatingRequestUseCase().collectLatest { reduce { it } }
            }
        },
    )

    fun perform(action: RatingAction) {
        logger.d { "Perform $action" }
        when (action) {
            is RatingAction.AskLaterClick -> onAskLaterClick()
            is RatingAction.DismissClick -> onAskLaterClick()
            is RatingAction.NeverAskAgainClick -> onNeverAskAgainClick()
            is RatingAction.RatingClick -> onRatingClick()
        }
    }

    private fun onAskLaterClick() = intent {
        postponeRatingRequestUseCase()
    }

    private fun onNeverAskAgainClick() = intent {
        disableRatingRequestUseCase()
    }

    private fun onRatingClick() = intent {
        val store = getRatingStoreUseCase.invoke()
        postSideEffect(RatingSideEffect.OpenLink(store.link))
        disableRatingRequestUseCase()
    }
}

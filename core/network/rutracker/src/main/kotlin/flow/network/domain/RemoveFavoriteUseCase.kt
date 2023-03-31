package flow.network.domain

import flow.network.api.RuTrackerInnerApi

internal class RemoveFavoriteUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
    private val withAuthorisedCheckUseCase: WithAuthorisedCheckUseCase,
    private val withFormTokenUseCase: WithFormTokenUseCase,
) {
    suspend operator fun invoke(token: String, id: String): Boolean {
        return withTokenVerificationUseCase(token) { validToken ->
            withAuthorisedCheckUseCase(api.mainPage(validToken)) { html ->
                withFormTokenUseCase(html) { formToken ->
                    api.removeFavorite(validToken, id, formToken)
                        .contains("Тема удалена")
                }
            }
        }
    }
}

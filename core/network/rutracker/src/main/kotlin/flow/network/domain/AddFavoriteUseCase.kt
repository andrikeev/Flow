package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.ResultDto

internal class AddFavoriteUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
    private val withAuthorisedCheckUseCase: WithAuthorisedCheckUseCase,
    private val withFormTokenUseCase: WithFormTokenUseCase,
) {
    suspend operator fun invoke(token: String, id: String): ResultDto<Boolean> = tryCatching {
        withTokenVerificationUseCase(token) { validToken ->
            withAuthorisedCheckUseCase(api.mainPage(validToken)) { html ->
                withFormTokenUseCase(html) { formToken ->
                    api.addFavorite(validToken, id, formToken)
                        .contains("Тема добавлена")
                        .toResult()
                }
            }
        }
    }
}

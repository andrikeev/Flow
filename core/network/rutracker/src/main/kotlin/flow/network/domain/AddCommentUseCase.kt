package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.ResultDto

internal class AddCommentUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
    private val withAuthorisedCheckUseCase: WithAuthorisedCheckUseCase,
    private val withFormTokenUseCase: WithFormTokenUseCase,
) {
    suspend operator fun invoke(
        token: String,
        topicId: String,
        message: String,
    ): ResultDto<Boolean> = tryCatching {
        withTokenVerificationUseCase(token) { validToken ->
            withAuthorisedCheckUseCase(api.mainPage(validToken)) { html ->
                withFormTokenUseCase(html) { formToken ->
                    api.postMessage(validToken, topicId, formToken, message)
                        .contains("Сообщение было успешно отправлено")
                        .toResult()
                }
            }
        }
    }
}

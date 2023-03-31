package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.FileDto

internal class GetTorrentFileUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
) {
    suspend operator fun invoke(token: String, id: String): FileDto {
        return withTokenVerificationUseCase(token) { validToken ->
            api.download(validToken, id)
        }
    }
}

package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.FileDto
import flow.network.dto.ResultDto

internal class GetTorrentFileUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
) {
    suspend operator fun invoke(token: String, id: String): ResultDto<FileDto> = tryCatching {
        withTokenVerificationUseCase(token) { validToken ->
            api.download(validToken, id).toResult()
        }
    }
}

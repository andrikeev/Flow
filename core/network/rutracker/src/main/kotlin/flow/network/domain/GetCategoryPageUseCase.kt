package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.forum.CategoryPageDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetCategoryPageUseCase(
    private val api: RuTrackerInnerApi,
    private val parser: RuTrackerParser,
) {

    suspend operator fun invoke(id: String, page: Int?): CategoryPageDto {
        val html = api.category(id, page)
        return when {
            !parser.isForumExists(html) -> throw NotFound
            !parser.isForumAvailableForUser(html) -> throw Forbidden
            else -> parser.parseCategoryPage(html, id)
        }
    }
}

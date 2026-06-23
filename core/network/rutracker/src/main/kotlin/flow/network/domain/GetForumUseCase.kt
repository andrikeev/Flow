package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.forum.ForumDto

internal class GetForumUseCase(
    private val api: RuTrackerInnerApi,
    private val parser: RuTrackerParser,
) {

    suspend operator fun invoke(): ForumDto {
        if (ForumCache.expired()) {
            ForumCache.cache = System.currentTimeMillis() to parser.parseForum(api.forum())
        }
        return ForumCache.cache!!.second
    }

    private object ForumCache {

        private const val ONE_MONTH: Long = 30L * 24 * 60 * 60 * 1000

        var cache: Pair<Long, ForumDto>? = null

        fun expired(): Boolean {
            return cache.let { it == null || System.currentTimeMillis() - it.first > ONE_MONTH }
        }
    }
}

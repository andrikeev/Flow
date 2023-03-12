package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.ResultDto
import flow.network.dto.forum.CategoryDto
import flow.network.dto.forum.ForumDto
import org.jsoup.Jsoup

internal class GetForumUseCase(private val api: RuTrackerInnerApi) {

    suspend operator fun invoke(): ResultDto<ForumDto> = tryCatching {
        if (ForumCache.expired()) {
            ForumCache.cache = System.currentTimeMillis() to parseForumTree(api.forum())
        }
        return ForumCache.cache!!.second.toResult()
    }

    companion object {
        private fun parseForumTree(html: String): ForumDto {
            val doc = Jsoup.parse(html)
            val categories = mutableListOf<CategoryDto>()
            val treeRoots = doc.select(".tree-root")
            treeRoots.forEach { categoryElement ->
                val title = categoryElement.select(".c-title").attr("title")
                val forums = mutableListOf<CategoryDto>()
                val forumElements = categoryElement.child(0).child(1).children()
                forumElements.forEach { forumElement ->
                    val forumId = forumElement.child(0).select("a").url()
                    val forumTitle = forumElement.child(0).select("a").toStr()
                    val subforums = mutableListOf<CategoryDto>()
                    if (forumElement.children().size > 1) {
                        val subforumElements = forumElement.child(1).children()
                        subforumElements.forEach { subforumElement ->
                            val subforumId = subforumElement.select("a").url()
                            val subforumTitle = subforumElement.toStr()
                            subforums.add(CategoryDto(id = subforumId, name = subforumTitle))
                        }
                    }
                    forums.add(CategoryDto(id = forumId, name = forumTitle, children = subforums))
                }
                categories.add(CategoryDto(name = title, children = forums))
            }
            return ForumDto(categories)
        }
    }

    private object ForumCache {

        private const val ONE_MONTH: Long = 30L * 24 * 60 * 60 * 1000

        var cache: Pair<Long, ForumDto>? = null

        fun expired(): Boolean {
            return cache.let { it == null || System.currentTimeMillis() - it.first > ONE_MONTH }
        }
    }
}

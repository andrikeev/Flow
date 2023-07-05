package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.forum.CategoryDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.ForumTopicDto
import flow.network.dto.topic.TopicDto
import flow.network.dto.topic.TorrentDto
import flow.network.model.Forbidden
import flow.network.model.NotFound
import org.jsoup.Jsoup

internal class GetCategoryPageUseCase(private val api: RuTrackerInnerApi) {

    suspend operator fun invoke(id: String, page: Int?): CategoryPageDto {
        val html = api.category(id, page)
        return when {
            !isForumExists(html) -> throw NotFound
            !isForumAvailableForUser(html) -> throw Forbidden
            else -> parseCategoryPage(html, id)
        }
    }

    companion object {

        private fun isForumExists(html: String): Boolean {
            return !html.contains("Ошибочный запрос: не задан forum_id") and
                !html.contains("Такого форума не существует")
        }

        private fun isForumAvailableForUser(html: String): Boolean {
            return !html.contains("Извините, только пользователи со специальными правами")
        }

        private fun parseCategoryPage(html: String, forumId: String): CategoryPageDto {
            val doc = Jsoup.parse(html)
            val currentPage = doc.select("#pagination > p:nth-child(1) > b:nth-child(1)").toInt(1)
            val totalPages = doc.select("#pagination > p:nth-child(1) > b:nth-child(2)").toInt(1)
            val forumName = doc.select(".maintitle").toStr()

            val subforumNodes = doc.select(".forumlink > a")
            val children = mutableListOf<CategoryDto>()
            for (subforumNode in subforumNodes) {
                val id = subforumNode.queryParam("f")
                val name = subforumNode.toStr()
                val subforum = CategoryDto(id, name)
                children.add(subforum)
            }

            val topicNodes = doc.select(".hl-tr")
            val topics = mutableListOf<ForumTopicDto>()
            for (topicNode in topicNodes) {
                val id = topicNode.select("td").attr("id")
                val authorId = topicNode.select("a.topicAuthor").queryParamOrNull("u")
                val authorName = topicNode.select("a.topicAuthor").toStr()
                val seeds = topicNode.select(".seedmed").toIntOrNull()
                val leeches = topicNode.select(".leechmed").toIntOrNull()
                val size = topicNode.select(".f-dl").text().replace("\u00a0", " ")
                val fullTitle = topicNode.select(".tt-text").toStr()
                val title = getTitle(fullTitle)
                val tags = getTags(fullTitle)
                val status = ParseTorrentStatusUseCase(topicNode)
                val author = if (authorName.isBlank()) {
                    AuthorDto(name = topicNode.select(".vf-col-author").toStr())
                } else {
                    AuthorDto(id = authorId, name = authorName)
                }
                if (status == null) {
                    topics.add(TopicDto(id, fullTitle, author))
                } else {
                    topics.add(
                        TorrentDto(
                            id = id,
                            title = title,
                            tags = tags,
                            status = status,
                            author = author,
                            size = size,
                            seeds = seeds,
                            leeches = leeches,
                        ),
                    )
                }
            }
            return CategoryPageDto(
                category = CategoryDto(forumId, forumName),
                page = currentPage,
                pages = totalPages,
                children = children,
                topics = topics,
            )
        }
    }
}

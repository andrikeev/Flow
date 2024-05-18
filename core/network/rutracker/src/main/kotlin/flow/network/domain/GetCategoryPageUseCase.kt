package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.forum.CategoryDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.SectionDto
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

            val sections = mutableListOf<SectionDto>()
            val topics = mutableListOf<ForumTopicDto>()
            var currentSection: String? = null
            val currentSectionIds: MutableList<String> = mutableListOf()
            val rows = doc.select("table.vf-table.forum > tbody > tr")
            rows.forEach { element ->
                if (element.children().any { it.hasClass("topicSep") }) {
                    currentSection?.let { name ->
                        sections.add(SectionDto(name, currentSectionIds.toList()))
                    }
                    currentSection = element.toStr()
                    currentSectionIds.clear()
                } else if (element.hasClass("hl-tr")) {
                    val id = element.select("td").attr("id")
                    val authorId = element.select("a.topicAuthor").queryParamOrNull("u")
                    val authorName = element.select("a.topicAuthor").toStr()
                    val seeds = element.select(".seedmed").toIntOrNull()
                    val leeches = element.select(".leechmed").toIntOrNull()
                    val size = element.select(".f-dl").text().replace("\u00a0", " ")
                    val fullTitle = element.select(".tt-text").toStr()
                    val title = getTitle(fullTitle)
                    val tags = getTags(fullTitle)
                    val status = ParseTorrentStatusUseCase(element)
                    val author = if (authorName.isBlank()) {
                        AuthorDto(name = element.select(".vf-col-author").toStr())
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
                    currentSectionIds.add(id)
                }
            }
            currentSection?.let { name ->
                sections.add(SectionDto(name, currentSectionIds.toList()))
            }
            return CategoryPageDto(
                category = CategoryDto(forumId, forumName),
                page = currentPage,
                pages = totalPages,
                sections = sections.takeIf { it.size > 1 } ?: emptyList(),
                children = children,
                topics = topics,
            )
        }
    }
}

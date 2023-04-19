package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.forum.CategoryDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.ForumTopicDto
import flow.network.dto.topic.TopicDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.user.FavoritesDto
import org.jsoup.Jsoup

internal class GetFavoritesUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
    private val withAuthorisedCheckUseCase: WithAuthorisedCheckUseCase,
) {

    suspend operator fun invoke(token: String): FavoritesDto {
        return withTokenVerificationUseCase(token) { validToken ->
            withAuthorisedCheckUseCase(api.favorites(validToken, 1)) { html ->
                val pagesCount = parsePagesCount(html)
                FavoritesDto(
                    (listOf(parseFavorites(html)) +
                            (2..pagesCount)
                                .map { page -> api.favorites(token, page) }
                                .map(::parseFavorites))
                        .flatten()
                )
            }
        }
    }

    companion object {
        private fun parsePagesCount(html: String): Int {
            val doc = Jsoup.parse(html)
            val navigation = doc.select("#pagination")
            val currentPage = navigation.select("b").toInt(1)
            return maxOf(
                navigation
                    .select(".pg")
                    .takeLast(2)
                    .firstOrNull()
                    .toInt(1),
                currentPage,
            )
        }

        private fun parseFavorites(html: String): List<ForumTopicDto> {
            return Jsoup
                .parse(html)
                .select(".hl-tr")
                .map { element ->
                    val id = element.select(".topic-selector").attr("data-topic_id")
                    val fullTitle = element.select(".torTopic.ts-text").toStr()
                    val title = getTitle(fullTitle)
                    val tags = getTags(fullTitle)
                    val status = ParseTorrentStatusUseCase(element)
                    val authorId = getIdFromUrl(element.select(".topicAuthor").urlOrNull(), "u")
                    val authorName = element.select(".topicAuthor > .topicAuthor").text()
                    val author = authorName.let { AuthorDto(id = authorId, name = it) }
                    val categoryId = requireIdFromUrl(
                        element.select(".t-forum-cell").select("a").last().url(),
                        "f"
                    )
                    val categoryName = element.select(".t-forum-cell > .ts-text").toStr()
                    val category = CategoryDto(categoryId, categoryName)
                    if (status != null) {
                        val size = element.select(".f-dl").text()
                        val seeds = element.select(".seedmed").toIntOrNull()
                        val leeches = element.select(".leechmed").toIntOrNull()
                        TorrentDto(
                            id = id,
                            title = title,
                            author = author,
                            category = category,
                            tags = tags,
                            status = status,
                            size = size,
                            seeds = seeds,
                            leeches = leeches
                        )
                    } else {
                        TopicDto(
                            id = id,
                            title = fullTitle,
                            author = author,
                            category = category
                        )
                    }
                }
        }
    }
}

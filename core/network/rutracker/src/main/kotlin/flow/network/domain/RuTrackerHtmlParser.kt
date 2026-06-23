package flow.network.domain

import com.fleeksoft.ksoup.Ksoup
import flow.network.dto.auth.CaptchaDto
import flow.network.dto.forum.CategoryDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.ForumDto
import flow.network.dto.forum.SectionDto
import flow.network.dto.search.SearchPageDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.CommentsPageDto
import flow.network.dto.topic.ForumTopicDto
import flow.network.dto.topic.TopicDto
import flow.network.dto.topic.TopicPageDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.topic.TorrentStatusDto
import flow.network.dto.user.ProfileDto
import java.util.regex.Pattern

/**
 * jsoup/ksoup-based [RuTrackerParser]. Parsing rules (CSS selectors, marker
 * strings) live here; the focused `Parse*UseCase` objects are kept as the
 * internal building blocks this implementation delegates to.
 */
internal class RuTrackerHtmlParser : RuTrackerParser {

    // region pages → DTO

    override fun parseSearchPage(html: String): SearchPageDto {
        val doc = Ksoup.parse(html)
        val navigation =
            doc.select("#main_content_wrap > div.bottom_info > div.nav > p:nth-child(1)")
        val currentPage = navigation.select("b:nth-child(1)").toInt(1)
        val totalPages = navigation.select("b:nth-child(2)").toInt(1)
        val torrents = doc.select(".hl-tr").map { element ->
            val id = element.select(".t-title > a").attr("data-topic_id")
            val status = ParseTorrentStatusUseCase(element) ?: TorrentStatusDto.Checking
            val titleWithTags = element.select(".t-title > a").toStr()
            val title = getTitle(titleWithTags)
            val tags = getTags(titleWithTags)
            val authorId = element.select(".u-name > a").queryParamOrNull("pid")
            val authorName = element.select(".u-name > a").text()
            val author = AuthorDto(id = authorId, name = authorName)
            val categoryId = element.select(".f").queryParam("f")
            val categoryName = element.select(".f").toStr()
            val size = formatSize(element.select(".tor-size").attr("data-ts_text").toLong())
            val date = element.select("[style]").attr("data-ts_text").toLongOrNull()
            val seeds = element.select(".seedmed").toIntOrNull()
            val leeches = element.select(".leechmed").toIntOrNull()
            TorrentDto(
                id = id,
                title = title,
                author = author,
                category = CategoryDto(categoryId, categoryName),
                tags = tags,
                status = status,
                date = date,
                size = size,
                seeds = seeds,
                leeches = leeches,
            )
        }
        return SearchPageDto(currentPage, totalPages, torrents)
    }

    override fun parseCategoryPage(html: String, categoryId: String): CategoryPageDto {
        val doc = Ksoup.parse(html)
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
            category = CategoryDto(categoryId, forumName),
            page = currentPage,
            pages = totalPages,
            sections = sections.takeIf { it.size > 1 } ?: emptyList(),
            children = children,
            topics = topics,
        )
    }

    override fun parseForum(html: String): ForumDto {
        val doc = Ksoup.parse(html)
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

    override fun parseTopicPage(html: String): TopicPageDto = ParseTopicPageUseCase(html)

    override fun parseCommentsPage(html: String): CommentsPageDto = ParseCommentsPageUseCase(html)

    override fun parseTorrent(html: String): TorrentDto = ParseTorrentUseCase(html)

    override fun parseProfile(html: String): ProfileDto {
        val doc = Ksoup.parse(html)
        return ProfileDto(
            id = doc.select("#profile-uname").attr("data-uid"),
            name = doc.select("#profile-uname").toStr(),
            avatarUrl = doc.select("#avatar-img > img").attr("src"),
        )
    }

    override fun parseCurrentUserId(html: String): String {
        return Ksoup.parse(html)
            .select("#logged-in-username")
            .queryParam("u")
    }

    override fun parseFavorites(html: String): List<ForumTopicDto> {
        return Ksoup
            .parse(html)
            .select(".hl-tr")
            .map { element ->
                val id = element.select(".topic-selector").attr("data-topic_id")
                val fullTitle = element.select(".torTopic.ts-text").toStr()
                val title = getTitle(fullTitle)
                val tags = getTags(fullTitle)
                val status = ParseTorrentStatusUseCase(element)
                val authorId = element.select(".topicAuthor").queryParamOrNull("u")
                val authorName = element.select(".topicAuthor > .topicAuthor").text()
                val author = AuthorDto(id = authorId, name = authorName)
                val categoryId =
                    element.select(".t-forum-cell").select("a").last().queryParam("f")
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
                        leeches = leeches,
                    )
                } else {
                    TopicDto(
                        id = id,
                        title = fullTitle,
                        author = author,
                        category = category,
                    )
                }
            }
    }

    override fun parseFavoritesPagesCount(html: String): Int {
        val doc = Ksoup.parse(html)
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

    override fun parseCaptcha(html: String): CaptchaDto? {
        val codeMatcher = CaptchaCodeRegex.matcher(html)
        val sidMatcher = CaptchaSidRegex.matcher(html)
        val urlMatcher = CaptchaUrlRegex.matcher(html)
        return if (codeMatcher.find() && sidMatcher.find() && urlMatcher.find()) {
            val captchaUrl = urlMatcher.group(1).let { url ->
                url.takeIf { it.contains("http") } ?: "https://${url.trim('/')}"
            }
            CaptchaDto(sidMatcher.group(1), codeMatcher.group(1), captchaUrl)
        } else {
            null
        }
    }

    override fun parseFormToken(html: String): String {
        val matcher = FormTokenRegex.matcher(html)
        return if (matcher.find()) matcher.group(1) else ""
    }

    // endregion

    // region state / markers

    override fun isAuthorized(html: String) = html.contains("logged-in-username")

    override fun isTorrentTopic(html: String) = html.contains("magnet:?")

    override fun isTopicExists(html: String) =
        !html.contains("Тема не найдена") &&
            !html.contains("Тема находится в мусорке") &&
            !html.contains("Ошибочный запрос: не указан topic_id")

    override fun isTopicModerated(html: String) =
        html.contains("Раздача ожидает проверки модератором")

    override fun isBlockedForRegion(html: String) =
        html.contains("Извините, раздача недоступна для вашего региона")

    override fun isForumExists(html: String) =
        !html.contains("Ошибочный запрос: не задан forum_id") and
            !html.contains("Такого форума не существует")

    override fun isForumAvailableForUser(html: String) =
        !html.contains("Извините, только пользователи со специальными правами")

    override fun hasLoginForm(html: String) = html.contains("login-form")

    override fun isWrongCredentials(html: String) = html.contains("неверный пароль")

    override fun isFavoriteAdded(html: String) = html.contains("Тема добавлена")

    override fun isFavoriteRemoved(html: String) = html.contains("Тема удалена")

    override fun isCommentSent(html: String) =
        html.contains("Сообщение было успешно отправлено")

    // endregion

    private companion object {
        val FormTokenRegex: Pattern = Pattern.compile("form_token: '(.*)',")
        val CaptchaCodeRegex: Pattern =
            Pattern.compile("<input[^>]*name=\"(cap_code_[^\"]+)\"[^>]*>")
        val CaptchaSidRegex: Pattern =
            Pattern.compile("<input[^>]*name=\"cap_sid\"[^>]*value=\"([^\"]+)\"[^>]*>")
        val CaptchaUrlRegex: Pattern =
            Pattern.compile("<img[^>]*src=\"([^\"]+/captcha/[^\"]+)\"[^>]*>")
    }
}

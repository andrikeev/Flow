package flow.network.domain

import flow.network.dto.auth.CaptchaDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.ForumDto
import flow.network.dto.search.SearchPageDto
import flow.network.dto.topic.CommentsPageDto
import flow.network.dto.topic.ForumTopicDto
import flow.network.dto.topic.TopicPageDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.user.ProfileDto

/**
 * Extracts structured data from raw rutracker HTML.
 *
 * Pure: every method takes an HTML [String] and returns data — no network, no IO.
 * This is the single seam that hides all markup/text knowledge, so the whole
 * parsing layer can be swapped (e.g. for a config-driven implementation) behind it.
 */
internal interface RuTrackerParser {

    // region pages → DTO
    fun parseSearchPage(html: String): SearchPageDto
    fun parseCategoryPage(html: String, categoryId: String): CategoryPageDto
    fun parseForum(html: String): ForumDto
    fun parseTopicPage(html: String): TopicPageDto
    fun parseCommentsPage(html: String): CommentsPageDto
    fun parseTorrent(html: String): TorrentDto
    fun parseProfile(html: String): ProfileDto
    fun parseCurrentUserId(html: String): String
    fun parseFavorites(html: String): List<ForumTopicDto>
    fun parseFavoritesPagesCount(html: String): Int
    fun parseCaptcha(html: String): CaptchaDto?
    fun parseFormToken(html: String): String
    // endregion

    // region state / markers
    fun isAuthorized(html: String): Boolean
    fun isTorrentTopic(html: String): Boolean
    fun isTopicExists(html: String): Boolean
    fun isTopicModerated(html: String): Boolean
    fun isBlockedForRegion(html: String): Boolean
    fun isForumExists(html: String): Boolean
    fun isForumAvailableForUser(html: String): Boolean
    fun hasLoginForm(html: String): Boolean
    fun isWrongCredentials(html: String): Boolean
    fun isFavoriteAdded(html: String): Boolean
    fun isFavoriteRemoved(html: String): Boolean
    fun isCommentSent(html: String): Boolean
    // endregion
}

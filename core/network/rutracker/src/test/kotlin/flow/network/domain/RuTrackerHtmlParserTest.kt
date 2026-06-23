package flow.network.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Characterization tests for [RuTrackerHtmlParser] against real rutracker pages
 * (see `src/test/resources/fixtures/`). Guest-viewable pages only for now —
 * search/favorites/current-user require authenticated fixtures.
 */
class RuTrackerHtmlParserTest {

    private val parser = RuTrackerHtmlParser()

    private fun fixture(name: String) = Fixtures.load(name)

    @Test
    fun `parseProfile extracts id, name and avatar`() {
        val profile = parser.parseProfile(fixture("profile.html"))

        assertEquals("227805", profile.id)
        assertEquals("filolya", profile.name)
        assertTrue("avatar should be resolved", profile.avatarUrl.isNotBlank())
    }

    @Test
    fun `parseForum returns a non-empty tree with nested forums`() {
        val forum = parser.parseForum(fixture("forum_index.html"))

        assertTrue("expected top-level categories", forum.children.isNotEmpty())
        assertTrue(
            "expected at least one category with forums",
            forum.children.any { !it.children.isNullOrEmpty() },
        )
    }

    @Test
    fun `parseCategoryPage extracts category name and topics`() {
        val page = parser.parseCategoryPage(fixture("category_page.html"), "2270")

        assertEquals("2270", page.category.id)
        assertTrue("category name should be resolved", page.category.name.isNotBlank())
        assertFalse("expected topics", page.topics.isNullOrEmpty())
    }

    @Test
    fun `parseTorrent extracts id, title and magnet`() {
        val torrent = parser.parseTorrent(fixture("topic_torrent.html"))

        assertEquals("6873596", torrent.id)
        assertTrue("title: ${torrent.title}", torrent.title.contains("Мандалорец"))
        assertTrue(torrent.magnetLink.orEmpty().startsWith("magnet:"))
    }

    @Test
    fun `parseTopicPage extracts torrent data and comments`() {
        val page = parser.parseTopicPage(fixture("topic_torrent.html"))

        assertEquals("6873596", page.id)
        assertTrue(page.title.contains("Мандалорец"))
        assertNotNull("torrentData expected for a torrent topic", page.torrentData)
        assertTrue(page.torrentData?.magnetLink.orEmpty().startsWith("magnet:"))
        assertTrue("expected first post", page.commentsPage.posts.isNotEmpty())
    }

    @Test
    fun `parseCommentsPage extracts title and posts with content`() {
        val page = parser.parseCommentsPage(fixture("comments_page.html"))

        assertEquals("5116006", page.id)
        assertEquals("Мобильные устройства", page.title)
        assertTrue("expected posts", page.posts.isNotEmpty())
        assertTrue("every post should have an id", page.posts.all { it.id.isNotBlank() })
        assertTrue("expected parsed post content", page.posts.any { it.children.isNotEmpty() })
    }

    @Test
    fun `topic existence and torrent detection`() {
        assertTrue(parser.isTopicExists(fixture("topic_torrent.html")))
        assertFalse(parser.isTopicExists(fixture("topic_not_found.html")))
        assertTrue(parser.isTorrentTopic(fixture("topic_torrent.html")))
    }

    @Test
    fun `forum existence detection`() {
        assertTrue(parser.isForumExists(fixture("category_page.html")))
        assertFalse(parser.isForumExists(fixture("forum_not_found.html")))
    }

    @Test
    fun `unauthenticated page is not authorized`() {
        assertFalse(parser.isAuthorized(fixture("guest_unauthorized.html")))
    }
}

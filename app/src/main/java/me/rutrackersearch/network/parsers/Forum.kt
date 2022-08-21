package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.ForumCategory
import me.rutrackersearch.models.forum.ForumItem
import me.rutrackersearch.models.forum.ForumTopic
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.network.utils.getIdFromUrl
import me.rutrackersearch.network.utils.requireIdFromUrl
import me.rutrackersearch.network.utils.toInt
import me.rutrackersearch.network.utils.toIntOrNull
import me.rutrackersearch.network.utils.toStr
import me.rutrackersearch.network.utils.url
import org.jsoup.Jsoup

internal fun parseForumPage(html: String): Page<ForumItem> {
    val doc = Jsoup.parse(html)
    val categories = doc.select(".forumlink > a").map { element ->
        ForumCategory(
            Category(
                id = requireIdFromUrl(element.url(), "f"),
                name = element.toStr(),
            )
        )
    }
    val topics = doc.select(".hl-tr").map { element ->
        val id = element.select("td").attr("id")
        val title = element.select(".tt-text").toStr()
        val status = parseTorrentStatus(element)
        val author = Author(
            id = getIdFromUrl(element.select("a.topicAuthor").url(), "u"),
            name = element.select("a.topicAuthor").toStr().takeIf(String::isNotBlank)
                ?: element.select(".vf-col-author").toStr(),
        )
        ForumTopic(
            if (status == null) {
                BaseTopic(
                    id = id,
                    title = title,
                    author = author,
                )
            } else {
                Torrent(
                    id = id,
                    title = getTitle(title),
                    tags = getTags(title),
                    status = status,
                    author = author,
                    seeds = element.select(".seedmed").toIntOrNull(),
                    leeches = element.select(".leechmed").toIntOrNull(),
                    size = element.select(".f-dl").text().replace("\u00a0", " "),
                )
            }
        )
    }
    return Page(
        items = categories + topics,
        page = doc.select("#pagination > p:nth-child(1) > b:nth-child(1)").toInt(1),
        pages = doc.select("#pagination > p:nth-child(1) > b:nth-child(2)").toInt(1),
    )
}

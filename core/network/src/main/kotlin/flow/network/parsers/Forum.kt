package flow.network.parsers

import flow.models.Page
import flow.models.forum.Category
import flow.models.forum.ForumItem
import flow.models.topic.Author
import flow.models.topic.BaseTopic
import flow.models.topic.Torrent
import flow.networkutils.getIdFromUrl
import flow.networkutils.requireIdFromUrl
import flow.networkutils.toInt
import flow.networkutils.toIntOrNull
import flow.networkutils.toStr
import flow.networkutils.url
import org.jsoup.Jsoup

internal fun parseForumPage(html: String): Page<ForumItem> {
    val doc = Jsoup.parse(html)
    val categories = doc.select(".forumlink > a").map { element ->
        ForumItem.Category(
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
        ForumItem.Topic(
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

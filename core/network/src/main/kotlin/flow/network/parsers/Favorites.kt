package flow.network.parsers

import flow.models.Page
import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.BaseTopic
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.networkutils.getIdFromUrl
import flow.networkutils.requireIdFromUrl
import flow.networkutils.toInt
import flow.networkutils.toIntOrNull
import flow.networkutils.toStr
import flow.networkutils.toStrOrNull
import flow.networkutils.url
import flow.networkutils.urlOrNull
import org.jsoup.Jsoup

fun parseFavoritesPage(data: String): Page<Topic> {
    val doc = Jsoup.parse(data)
    val navigation = doc.select("#pagination")
    val currentPage = navigation.select("b").toInt(1)
    val totalPages = maxOf(navigation.select(".pg").takeLast(2).firstOrNull().toInt(1), currentPage)
    return Page(
        items = doc.select(".hl-tr").map { element ->
            val id = element.select(".topic-selector").attr("data-topic_id")
            val fullTitle = element.select(".torTopic.ts-text").toStr()
            val title = getTitle(fullTitle)
            val tags = getTags(fullTitle)
            val status = parseTorrentStatus(element)
            val authorId = getIdFromUrl(element.select(".topicAuthor").urlOrNull(), "u")
            val authorName = element.select(".topicAuthor > .topicAuthor").toStrOrNull()
            val author = authorName?.let { Author(id = authorId, name = it) }
            val categoryId = requireIdFromUrl(element.select(".t-forum-cell").select("a").last().url(), "f")
            val categoryName = element.select(".t-forum-cell > .ts-text").toStr()
            val category = Category(categoryId, categoryName)
            if (status != null) {
                val size = element.select(".f-dl").toStrOrNull()
                val seeds = element.select(".seedmed").toIntOrNull()
                val leeches = element.select(".leechmed").toIntOrNull()
                Torrent(
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
                BaseTopic(
                    id = id,
                    title = fullTitle,
                    author = author,
                    category = category
                )
            }
        },
        page = currentPage,
        pages = totalPages,
    )
}
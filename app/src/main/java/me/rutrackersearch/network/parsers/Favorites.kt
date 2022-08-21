package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.network.utils.getIdFromUrl
import me.rutrackersearch.network.utils.requireIdFromUrl
import me.rutrackersearch.network.utils.toInt
import me.rutrackersearch.network.utils.toIntOrNull
import me.rutrackersearch.network.utils.toStr
import me.rutrackersearch.network.utils.toStrOrNull
import me.rutrackersearch.network.utils.url
import me.rutrackersearch.network.utils.urlOrNull
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
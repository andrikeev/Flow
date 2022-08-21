package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.models.topic.TorrentStatus
import me.rutrackersearch.network.utils.getIdFromUrl
import me.rutrackersearch.network.utils.requireIdFromUrl
import me.rutrackersearch.network.utils.toInt
import me.rutrackersearch.network.utils.toIntOrNull
import me.rutrackersearch.network.utils.toStr
import me.rutrackersearch.network.utils.toStrOrNull
import me.rutrackersearch.network.utils.url
import me.rutrackersearch.network.utils.urlOrNull
import org.jsoup.Jsoup

fun parseSearchPage(data: String): Page<Torrent> {
    val doc = Jsoup.parse(data)
    val navigation = doc.select("#main_content_wrap > div.bottom_info > div.nav > p:nth-child(1)")
    return Page(
        items = doc.select(".hl-tr").map { element ->
            val id = element.select(".t-title > a").attr("data-topic_id")
            val status = parseTorrentStatus(element) ?: TorrentStatus.CHECKING
            val titleWithTags = element.select(".t-title > a").toStr()
            val title = getTitle(titleWithTags)
            val tags = getTags(titleWithTags)
            val authorId = getIdFromUrl(element.select(".u-name > a").urlOrNull(), "pid")
            val authorName = element.select(".u-name > a").toStrOrNull()
            val author = authorName?.let { Author(id = authorId, name = it) }
            val categoryId = requireIdFromUrl(element.select(".f").url(), "f")
            val categoryName = element.select(".f").toStr()
            val size = formatSize(element.select(".tor-size").attr("data-ts_text").toLong())
            val date = element.select("[style]").attr("data-ts_text").toLongOrNull()
            val seeds = element.select(".seedmed").toIntOrNull()
            val leeches = element.select(".leechmed").toIntOrNull()
            Torrent(
                id = id,
                title = title,
                author = author,
                category = Category(categoryId, categoryName),
                tags = tags,
                status = status,
                date = date,
                size = size,
                seeds = seeds,
                leeches = leeches,
            )
        },
        page = navigation.select("b:nth-child(1)").toInt(1),
        pages = navigation.select("b:nth-child(2)").toInt(1),
    )
}

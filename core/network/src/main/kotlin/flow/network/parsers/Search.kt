package flow.network.parsers

import flow.models.Page
import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.Torrent
import flow.models.topic.TorrentStatus
import flow.networkutils.getIdFromUrl
import flow.networkutils.requireIdFromUrl
import flow.networkutils.toInt
import flow.networkutils.toIntOrNull
import flow.networkutils.toStr
import flow.networkutils.toStrOrNull
import flow.networkutils.url
import flow.networkutils.urlOrNull
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

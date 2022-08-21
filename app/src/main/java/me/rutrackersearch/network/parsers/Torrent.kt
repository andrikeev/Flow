package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.models.topic.TorrentDescription
import me.rutrackersearch.models.topic.TorrentStatus
import me.rutrackersearch.network.utils.getIdFromUrl
import me.rutrackersearch.network.utils.requireIdFromUrl
import me.rutrackersearch.network.utils.toIntOrNull
import me.rutrackersearch.network.utils.toStr
import me.rutrackersearch.network.utils.toStrOrNull
import me.rutrackersearch.network.utils.url
import me.rutrackersearch.network.utils.urlOrNull
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

internal fun parseTorrent(data: String): Torrent = Jsoup.parse(data).let { doc ->
    val isAuthorized = isAuthorisedPage(data)
    val categoryNode = doc.select(".nav.w100.pad_2").select("a").last()
    val header = doc.select("table.forumline.dl_list > tbody > tr")
    return Torrent(
        id = requireIdFromUrl(doc.select("#topic-title").url(), "t"),
        title = getTitle(doc.select("#topic-title").toStr()),
        tags = getTags(doc.select("#topic-title").toStr()),
        status = parseTorrentStatus(doc.select("#tor-status-resp").first()),
        category = Category(
            id = requireIdFromUrl(categoryNode.url(), "f"),
            name = categoryNode.toStr(),
        ),
        author = doc.select(".nick").first().toStrOrNull()?.let {
            val authorId = getIdFromUrl(doc.select(".poster_btn").select(".txtb").first().urlOrNull(), "u")
            Author(id = authorId, name = it)
        },
        seeds = header.select(".seed > b").toIntOrNull(),
        leeches = header.select(".leech > b").toIntOrNull(),
        size = if (isAuthorized) {
            doc.select("#tor-size-humn").toStr()
        } else {
            doc.select(".attach_link > ul > li:nth-child(2)").toStr()
        },
        magnetLink = doc.select(".magnet-link").url(),
        description = parseTorrentDescription(data)
    )
}

internal fun parseTorrentStatus(element: Element?): TorrentStatus? {
    return element?.let {
        when {
            element.select(".tor-dup").isNotEmpty() -> TorrentStatus.DUPLICATE
            element.select(".tor-not-approved").isNotEmpty() -> TorrentStatus.NOT_APPROVED
            element.select(".tor-checking").isNotEmpty() -> TorrentStatus.CHECKING
            element.select(".tor-approved").isNotEmpty() -> TorrentStatus.APPROVED
            element.select(".tor-need-edit").isNotEmpty() -> TorrentStatus.NEED_EDIT
            element.select(".tor-closed").isNotEmpty() -> TorrentStatus.CLOSED
            element.select(".tor-no-desc").isNotEmpty() -> TorrentStatus.NO_DESCRIPTION
            element.select(".tor-consumed").isNotEmpty() -> TorrentStatus.CONSUMED
            else -> null
        }
    }
}

internal fun parseTorrentDescription(data: String): TorrentDescription = runCatching {
    parseContent(requireNotNull(Jsoup.parse(data).select("tbody[id^=post]").first()))
}.fold(
    onSuccess = { TorrentDescription(it) },
    onFailure = { TorrentDescription(emptyList<PostElement>().parseContent()) },
)

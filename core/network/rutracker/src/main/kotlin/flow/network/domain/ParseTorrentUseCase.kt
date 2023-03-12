package flow.network.domain

import flow.network.dto.forum.CategoryDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.TorrentDescriptionDto
import flow.network.dto.topic.TorrentDto
import org.jsoup.Jsoup

internal object ParseTorrentUseCase {
    operator fun invoke(html: String): TorrentDto {
        val doc = Jsoup.parse(html)
        val id = requireIdFromUrl(doc.select("#topic-title").url(), "t")
        val author = doc.select(".nick").first()?.text()?.let {
            val authorId =
                getIdFromUrl(doc.select(".poster_btn").select(".txtb").first().urlOrNull(), "u")
            AuthorDto(id = authorId, name = it)
        }
        val title = getTitle(doc.select("#topic-title").toStr())
        val tags = getTags(doc.select("#topic-title").toStr())
        val categoryNode = doc.select(".nav.w100.pad_2").select("a").last()
        val categoryId = requireIdFromUrl(categoryNode.url(), "f")
        val categoryName = categoryNode.toStr()
        val magnetLink = doc.select(".magnet-link").url()
        val header = doc.select("table.forumline.dl_list > tbody > tr")
        val seeds = header.select(".seed > b").toIntOrNull()
        val leeches = header.select(".leech > b").toIntOrNull()
        val status = ParseTorrentStatusUseCase(doc.select("#tor-status-resp").first())
        val size = if (html.contains("logged-in-username")) {
            doc.select("#tor-size-humn").toStr()
        } else {
            doc.select(".attach_link > ul > li:nth-child(2)").toStr()
        }
        return TorrentDto(
            id = id,
            title = title,
            tags = tags,
            status = status,
            category = CategoryDto(categoryId, categoryName),
            author = author,
            seeds = seeds,
            leeches = leeches,
            size = size,
            magnetLink = magnetLink,
            description = parseTorrentDescription(html)
        )
    }

    private fun parseTorrentDescription(html: String): TorrentDescriptionDto {
        val doc = Jsoup.parse(html)
        return try {
            val firstPost = doc.select("tbody[id^=post]").first()?.select(".post_body")
            TorrentDescriptionDto(ParsePostUseCase(firstPost))
        } catch (e: Throwable) {
            TorrentDescriptionDto(emptyList())
        }
    }
}

package flow.network.domain

import flow.network.dto.forum.CategoryDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.PostDto
import flow.network.dto.topic.TopicPageCommentsDto
import flow.network.dto.topic.TopicPageDto
import flow.network.dto.topic.TorrentDataDto
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal object ParseTopicPageUseCase {
    operator fun invoke(html: String) = Jsoup.parse(html).let { doc ->
        TopicPageDto(
            id = doc.parseId(),
            title = doc.parseTitle(),
            author = doc.parseAuthor(),
            category = doc.parseCategory(),
            torrentData = doc.parseTorrentData(),
            commentsPage = doc.parseCommentsPage(),
        )
    }

    private fun Document.parseId() = select("#topic-title").queryParam("t")

    private fun Document.parseTitle() = getTitle(select("#topic-title").toStr())

    private fun Document.parseTags() = getTags(select("#topic-title").toStr())

    private fun Document.parseAuthor() = selectFirstPost()?.parseAuthor()

    private fun Document.parseCategory() =
        select(".nav.w100.pad_2").select("a").last().let { element ->
            CategoryDto(
                id = element.queryParam("f"),
                name = element.toStr(),
            )
        }

    private fun Document.parseTorrentData(): TorrentDataDto? {
        val header = select("table.forumline.dl_list > tbody > tr")
        val seeds = header.select(".seed > b").toIntOrNull()
        val leeches = header.select(".leech > b").toIntOrNull()
        val status = ParseTorrentStatusUseCase(select("#tor-status-resp").first())
        val size = if (selectFirst("#logged-in-username") != null) {
            select("#tor-size-humn").toStrOrNull()
        } else {
            select(".attach_link > ul > li:nth-child(2)").toStrOrNull()
        }
        val firstPost = selectFirstPost()
        val date = firstPost?.parseDate()
        val posterUrl = firstPost
            ?.select(".postImg.postImgAligned.img-right")
            ?.attr("title")
        val magnetLink = select(".magnet-link").url()

        return if (seeds != null || leeches != null || status != null || !size.isNullOrBlank()) {
            TorrentDataDto(
                tags = parseTags(),
                posterUrl = posterUrl,
                status = status,
                date = date,
                size = size,
                seeds = seeds,
                leeches = leeches,
                magnetLink = magnetLink,
            )
        } else {
            null
        }
    }

    private fun Document.parseCommentsPage() = parsePages().let { (page, pages) ->
        TopicPageCommentsDto(
            page = page,
            pages = pages,
            posts = parsePosts(),
        )
    }

    private fun Document.parsePages() =
        select("#pagination > tbody > tr > td > p:nth-child(1)").let {
            it.select("b:nth-child(1)").toInt(1) to it.select("b:nth-child(2)").toInt(1)
        }

    private fun Document.parsePosts() = select("tbody[id^=post]").map { it.parsePost() }

    private fun Document.selectFirstPost() = selectFirst("tbody[id^=post]")

    private fun Element.parsePost() = PostDto(
        id = parseId(),
        author = parseAuthor(),
        date = parseDate(),
        children = parseContent(),
    )

    private fun Element.parseId() = select(".post_body").attr("id").substringAfter("p-")

    private fun Element.parseAuthor() = AuthorDto(
        id = select(".poster_btn").select(".txtb").first().queryParamOrNull("u"),
        name = select(".nick").text(),
        avatarUrl = select(".avatar > img").attr("src"),
    )

    private fun Element.parseDate() = select(".p-link").text()

    private fun Element.parseContent() = ParsePostUseCase(select(".post_body"))
}

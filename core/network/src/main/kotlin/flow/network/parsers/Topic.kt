package flow.network.parsers

import flow.models.Page
import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.BaseTopic
import flow.models.topic.Post
import flow.models.topic.Topic
import flow.networkutils.getIdFromUrl
import flow.networkutils.requireIdFromUrl
import flow.networkutils.toInt
import flow.networkutils.toStr
import flow.networkutils.url
import flow.networkutils.urlOrNull
import org.jsoup.Jsoup

fun parseTopic(html: String): Topic = if (html.contains("magnet-link")) {
    parseTorrent(html)
} else {
    Jsoup.parse(html).let { doc ->
        BaseTopic(
            id = requireIdFromUrl(doc.select("#topic-title").url(), "t"),
            title = doc.select("#topic-title").toStr(),
            category = doc.select(".nav.w100.pad_2").select("a").let { elements ->
                Category(
                    id = requireIdFromUrl(elements.last().url(), "f"),
                    name = elements.last().toStr(),
                )
            },
        )
    }
}

fun parseComments(html: String): Page<Post> = Jsoup.parse(html).let { doc ->
    val firstPost = doc.select("tbody[id^=post]").first()
    if (!firstPost?.select(".magnet-link").urlOrNull().isNullOrEmpty()) {
        firstPost?.remove()
    }
    val navigation = doc.select("#pagination > tbody > tr > td > p:nth-child(1)")
    return Page(
        items = doc.select("tbody[id^=post]").map { post ->
            Post(
                id = post.select(".post_body").attr("id").substringAfter("p-"),
                author = Author(
                    id = getIdFromUrl(post.select(".poster_btn").select(".txtb").first().urlOrNull(), "u"),
                    name = post.select(".nick").text(),
                    avatarUrl = post.select(".avatar > img").attr("src")
                ),
                date = post.select(".p-link").text(),
                content = parseContent(post),
            )
        },
        page = navigation.select("b:nth-child(1)").toInt(1),
        pages = navigation.select("b:nth-child(2)").toInt(1),
    )
}

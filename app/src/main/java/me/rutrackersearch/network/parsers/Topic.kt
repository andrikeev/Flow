package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Post
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.network.utils.getIdFromUrl
import me.rutrackersearch.network.utils.requireIdFromUrl
import me.rutrackersearch.network.utils.toInt
import me.rutrackersearch.network.utils.toStr
import me.rutrackersearch.network.utils.url
import me.rutrackersearch.network.utils.urlOrNull
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

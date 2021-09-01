package me.rutrackersearch.data.converters

import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.topic.Author
import me.rutrackersearch.domain.entity.topic.BaseTopic
import me.rutrackersearch.domain.entity.topic.Post
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.entity.topic.TorrentStatus
import org.json.JSONObject

fun JSONObject.parseTopic(): Topic {
    val id = getString("id")
    val title = getString("title")
    val author = optJSONObject("author")?.parseAuthor()
    val category = optJSONObject("category")?.parseCategory()
    val tags = optionalString("tags")
    val status = optionalEnum<TorrentStatus>("status")
    val size = optionalString("size")
    val seeds = optionalInt("seeds")
    val leeches = optionalInt("leeches")
    return if (tags == null && status == null && size == null && seeds == null && leeches == null) {
        BaseTopic(
            id = id,
            title = title,
            author = author,
            category = category,
        )
    } else {
        Torrent(
            id = id,
            title = title,
            author = author,
            category = category,
            tags = tags,
            status = status,
            date = optionalLong("date"),
            size = size,
            seeds = seeds,
            leeches = leeches,
            magnetLink = optionalString("magnetLink"),
            description = optJSONObject("description")?.parseTorrentDescription(),
        )
    }
}

fun JSONObject.parseTopicPage(): Page<Post> {
    return Page(
        items = getJSONArray("posts").parseList(JSONObject::parsePost),
        page = getInt("page"),
        pages = getInt("pages"),
    )
}

fun JSONObject.parseAuthor(): Author {
    return Author(
        id = optionalString("id"),
        name = getString("name"),
        avatarUrl = optionalString("avatarUrl"),
    )
}

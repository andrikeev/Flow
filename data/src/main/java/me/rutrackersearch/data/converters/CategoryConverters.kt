package me.rutrackersearch.data.converters

import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.forum.ForumCategory
import me.rutrackersearch.domain.entity.forum.ForumItem
import me.rutrackersearch.domain.entity.forum.ForumTopic
import me.rutrackersearch.domain.entity.topic.BaseTopic
import me.rutrackersearch.domain.entity.topic.Torrent
import org.json.JSONObject

fun JSONObject.parseCategoryPage(): Page<ForumItem> {
    val category = parseCategory()
    val categories = getJSONArray("children").parseList(JSONObject::parseCategory)
    val topics = getJSONArray("topics").parseList(JSONObject::parseTopic)
    val items = categories.map(::ForumCategory) + topics.map { topic ->
        when (topic) {
            is BaseTopic -> ForumTopic(topic.copy(category = category))
            is Torrent -> ForumTopic(topic.copy(category = category))
        }
    }
    return Page(
        items = items,
        page = getInt("page"),
        pages = getInt("pages"),
    )
}

fun JSONObject.parseCategory(): Category {
    return Category(
        id = getString("id"),
        name = getString("name"),
    )
}

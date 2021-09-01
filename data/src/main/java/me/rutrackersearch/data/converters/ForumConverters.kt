package me.rutrackersearch.data.converters

import me.rutrackersearch.domain.entity.forum.ForumTree
import me.rutrackersearch.domain.entity.forum.ForumTreeGroup
import me.rutrackersearch.domain.entity.forum.ForumTreeRootGroup
import org.json.JSONObject

fun JSONObject.parseForumTree(): ForumTree {
    return ForumTree(
        children = getJSONArray("children").parseList(JSONObject::parseForumTreeRootGroup),
    )
}

private fun JSONObject.parseForumTreeRootGroup(): ForumTreeRootGroup {
    return ForumTreeRootGroup(
        name = getString("name"),
        children = getJSONArray("children").parseList(JSONObject::parseForumTreeGroup),
    )
}

private fun JSONObject.parseForumTreeGroup(): ForumTreeGroup {
    return ForumTreeGroup(
        category = parseCategory(),
        children = getJSONArray("children").parseList(JSONObject::parseCategory),
    )
}

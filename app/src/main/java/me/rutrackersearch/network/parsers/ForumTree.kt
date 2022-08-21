package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.ForumTree
import me.rutrackersearch.models.forum.ForumTreeGroup
import me.rutrackersearch.models.forum.ForumTreeRootGroup
import me.rutrackersearch.network.utils.toStr
import me.rutrackersearch.network.utils.url
import org.jsoup.Jsoup

fun parseForumTree(html: String): ForumTree = ForumTree(
    children = Jsoup.parse(html).select(".tree-root").map { rootElement ->
        ForumTreeRootGroup(
            name = rootElement.select(".c-title").attr("title"),
            children = rootElement.child(0).child(1).children().map { forumElement ->
                ForumTreeGroup(
                    category = Category(
                        id = forumElement.child(0).select("a").url(),
                        name = forumElement.child(0).select("a").toStr(),
                    ),
                    children = forumElement
                        .takeIf { forumElement.children().size > 1 }
                        ?.child(1)
                        ?.children()
                        ?.map { categoryElement ->
                            Category(
                                id = categoryElement.select("a").url(),
                                name = categoryElement.toStr(),
                            )
                        }
                        .orEmpty()
                )
            }
        )
    }
)

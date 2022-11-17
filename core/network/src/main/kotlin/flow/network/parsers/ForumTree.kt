package flow.network.parsers

import flow.models.forum.Category
import flow.models.forum.ForumTree
import flow.models.forum.ForumTreeGroup
import flow.models.forum.ForumTreeRootGroup
import flow.networkutils.toStr
import flow.networkutils.url
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

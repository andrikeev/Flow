package flow.network

import flow.models.Page
import flow.models.forum.ForumItem
import flow.models.forum.ForumTree
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Post
import flow.models.topic.Topic
import flow.models.topic.Torrent

interface NetworkApi {
    suspend fun search(
        query: String,
        sort: Sort,
        order: Order,
        period: Period,
        author: String,
        authorId: String,
        categories: String,
        page: Int = 1,
    ): Page<Torrent>

    suspend fun forumTree(): ForumTree

    suspend fun category(id: String, page: Int = 1): Page<ForumItem>

    suspend fun favorites(page: Int = 1): Page<Topic>

    suspend fun addFavorite(id: String)

    suspend fun removeFavorite(id: String)

    suspend fun topic(id: String, pid: String): Topic

    suspend fun comments(id: String, page: Int = 1): Page<Post>

    suspend fun addComment(topicId: String, message: String)

    suspend fun torrent(id: String): Torrent
}

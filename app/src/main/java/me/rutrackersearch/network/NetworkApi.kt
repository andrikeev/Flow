package me.rutrackersearch.network

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.ForumItem
import me.rutrackersearch.models.forum.ForumTree
import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.models.topic.Post
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

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

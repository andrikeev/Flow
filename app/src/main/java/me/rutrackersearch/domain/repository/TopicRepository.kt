package me.rutrackersearch.domain.repository

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.topic.Post
import me.rutrackersearch.models.topic.Topic

interface TopicRepository {
    suspend fun loadTopic(id: String, pid: String): Topic
    suspend fun loadCommentsPage(id: String, page: Int): Page<Post>
    suspend fun addComment(topicId: String, message: String)
}

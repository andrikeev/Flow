package me.rutrackersearch.domain.repository

import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.topic.Post
import me.rutrackersearch.domain.entity.topic.Topic

interface TopicRepository {
    suspend fun loadTopic(id: String, pid: String): Topic
    suspend fun loadCommentsPage(id: String, page: Int): Page<Post>
    suspend fun addComment(topicId: String, message: String)
}

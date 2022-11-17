package flow.data.api

import flow.models.topic.Post
import flow.models.topic.Topic

interface TopicRepository {
    suspend fun loadTopic(id: String, pid: String): Topic
    suspend fun loadCommentsPage(id: String, page: Int): flow.models.Page<Post>
    suspend fun addComment(topicId: String, message: String)
}

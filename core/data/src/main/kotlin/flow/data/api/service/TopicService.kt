package flow.data.api.service

import flow.models.Page
import flow.models.topic.Post
import flow.models.topic.Topic

interface TopicService {
    suspend fun getTopic(id: String): Topic
    suspend fun getCommentsPage(id: String, page: Int): Page<Post>
    suspend fun addComment(topicId: String, message: String): Boolean
}

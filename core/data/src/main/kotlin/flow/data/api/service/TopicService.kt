package flow.data.api.service

import flow.models.topic.Post
import flow.models.topic.Topic

interface TopicService {
    suspend fun getTopic(id: String, pid: String): Topic
    suspend fun getCommentsPage(id: String, page: Int): flow.models.Page<Post>
    suspend fun addComment(topicId: String, message: String)
}

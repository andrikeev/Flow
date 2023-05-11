package flow.data.api.service

import flow.models.Page
import flow.models.topic.Post
import flow.models.topic.Topic
import flow.models.topic.TopicPage

interface TopicService {
    suspend fun getTopic(id: String): Topic
    suspend fun getTopicPage(id: String, page: Int? = null): TopicPage
    suspend fun getCommentsPage(id: String, page: Int): Page<Post>
    suspend fun addComment(topicId: String, message: String): Boolean
}

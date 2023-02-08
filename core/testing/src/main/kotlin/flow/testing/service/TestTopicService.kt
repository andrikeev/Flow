package flow.testing.service

import flow.data.api.service.TopicService
import flow.models.Page
import flow.models.topic.Post
import flow.models.topic.Topic

class TestTopicService : TopicService {
    override suspend fun getTopic(id: String, pid: String): Topic {
        TODO("Not yet implemented")
    }

    override suspend fun getCommentsPage(id: String, page: Int): Page<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun addComment(topicId: String, message: String) {
        TODO("Not yet implemented")
    }
}

package flow.testing.repository

import flow.data.api.TopicRepository
import flow.models.Page
import flow.models.topic.Post
import flow.models.topic.Topic

class TestTopicRepository : TopicRepository {
    override suspend fun loadTopic(id: String, pid: String): Topic {
        TODO("Not yet implemented")
    }

    override suspend fun loadCommentsPage(id: String, page: Int): Page<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun addComment(topicId: String, message: String) {
        TODO("Not yet implemented")
    }
}

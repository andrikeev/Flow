package flow.data.impl

import flow.data.api.TopicRepository
import flow.models.Page
import flow.models.topic.Post
import flow.models.topic.Topic
import flow.network.NetworkApi
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val networkApi: NetworkApi,
) : TopicRepository {
    override suspend fun loadTopic(id: String, pid: String): Topic {
        return networkApi.topic(id, pid)
    }

    override suspend fun loadCommentsPage(id: String, page: Int): Page<Post> {
        return networkApi.comments(id, page)
    }

    override suspend fun addComment(topicId: String, message: String) {
        networkApi.addComment(topicId, message)
    }
}

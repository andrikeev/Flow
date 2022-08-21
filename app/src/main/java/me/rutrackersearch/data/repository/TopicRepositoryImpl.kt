package me.rutrackersearch.data.repository

import me.rutrackersearch.domain.repository.TopicRepository
import me.rutrackersearch.models.Page
import me.rutrackersearch.models.topic.Post
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.network.NetworkApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRepositoryImpl @Inject constructor(
    private val api: NetworkApi,
) : TopicRepository {
    override suspend fun loadTopic(id: String, pid: String): Topic {
        return api.topic(id, pid)
    }

    override suspend fun loadCommentsPage(id: String, page: Int): Page<Post> {
        return api.comments(id, page)
    }

    override suspend fun addComment(topicId: String, message: String) {
        api.addComment(topicId, message)
    }
}

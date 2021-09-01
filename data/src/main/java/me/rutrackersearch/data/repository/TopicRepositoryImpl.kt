package me.rutrackersearch.data.repository

import me.rutrackersearch.data.converters.parseTopic
import me.rutrackersearch.data.converters.parseTopicPage
import me.rutrackersearch.data.converters.readJson
import me.rutrackersearch.data.converters.toFailure
import me.rutrackersearch.data.network.ServerApi
import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.topic.Post
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.repository.TopicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : TopicRepository {

    override suspend fun loadTopic(id: String, pid: String): Topic {
        try {
            return api.topic(id, pid).readJson().parseTopic()
        } catch (e: Exception) {
            throw e.toFailure()
        }
    }

    override suspend fun loadCommentsPage(id: String, page: Int): Page<Post> {
        try {
            return api.comments(id, page).readJson().parseTopicPage()
        } catch (e: Exception) {
            throw e.toFailure()
        }
    }

    override suspend fun addComment(topicId: String, message: String) {
        try {
            api.addComment(topicId, message)
        } catch (e: Exception) {
            throw e.toFailure()
        }
    }
}

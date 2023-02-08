package flow.data.impl.service

import flow.auth.api.TokenProvider
import flow.data.api.service.TopicService
import flow.data.converters.toCommentsPage
import flow.data.converters.toTopic
import flow.models.Page
import flow.models.topic.Post
import flow.models.topic.Topic
import flow.network.api.NetworkApi
import javax.inject.Inject

class TopicServiceImpl @Inject constructor(
    private val networkApi: NetworkApi,
    private val tokenProvider: TokenProvider,
) : TopicService {
    override suspend fun getTopic(id: String, pid: String): Topic {
        return networkApi.getTopic(tokenProvider.getToken(), id, pid, null).toTopic()
    }

    override suspend fun getCommentsPage(id: String, page: Int): Page<Post> {
        return networkApi.getCommentsPage(tokenProvider.getToken(), id, null, page).toCommentsPage()
    }

    override suspend fun addComment(topicId: String, message: String) {
        networkApi.addComment(tokenProvider.getToken(), topicId, message)
    }
}

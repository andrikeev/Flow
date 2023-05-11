package flow.data.impl.service

import flow.auth.api.TokenProvider
import flow.data.api.service.TopicService
import flow.data.converters.toCommentsPage
import flow.data.converters.toTopic
import flow.data.converters.toTopicPage
import flow.models.Page
import flow.models.topic.Post
import flow.models.topic.Topic
import flow.models.topic.TopicPage
import flow.network.api.NetworkApi
import javax.inject.Inject

class TopicServiceImpl @Inject constructor(
    private val networkApi: NetworkApi,
    private val tokenProvider: TokenProvider,
) : TopicService {
    override suspend fun getTopic(id: String): Topic {
        return networkApi.getTopic(tokenProvider.getToken(), id, null).toTopic()
    }

    override suspend fun getTopicPage(id: String, page: Int?): TopicPage {
        return networkApi.getTopicPage(tokenProvider.getToken(), id, page).toTopicPage()
    }

    override suspend fun getCommentsPage(id: String, page: Int): Page<Post> {
        return networkApi.getTopicPage(tokenProvider.getToken(), id, page).toCommentsPage()
    }

    override suspend fun addComment(topicId: String, message: String): Boolean {
        return networkApi.addComment(tokenProvider.getToken(), topicId, message)
    }
}

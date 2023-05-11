package flow.domain.usecase

import flow.data.api.service.TopicService
import flow.domain.model.PagingAction
import flow.domain.model.PagingData
import flow.domain.model.PagingDataLoader
import flow.domain.model.refresh
import flow.logger.api.LoggerFactory
import flow.models.topic.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ObserveTopicPagingDataUseCase @Inject constructor(
    private val topicService: TopicService,
    private val loggerFactory: LoggerFactory,
) {
    suspend operator fun invoke(
        id: String,
        actions: Flow<PagingAction>,
        scope: CoroutineScope,
    ): Flow<PagingData<List<Post>>> {
        return PagingDataLoader(
            fetchData = { page -> topicService.getTopicPage(id, page).commentsPage },
            transform = { posts -> flowOf(posts) },
            actions = actions.onStart { refresh() },
            scope = scope,
            logger = loggerFactory.get("TopicPagingDataLoader"),
        ).flow
    }
}

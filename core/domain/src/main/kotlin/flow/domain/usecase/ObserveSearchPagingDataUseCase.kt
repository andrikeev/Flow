package flow.domain.usecase

import flow.data.api.service.SearchService
import flow.domain.model.PagingAction
import flow.domain.model.PagingData
import flow.domain.model.PagingDataLoader
import flow.domain.model.refresh
import flow.logger.api.LoggerFactory
import flow.models.search.Filter
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ObserveSearchPagingDataUseCase @Inject constructor(
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val searchService: SearchService,
    private val loggerFactory: LoggerFactory,
) {
    operator fun invoke(
        filterFlow: Flow<Filter>,
        actionsFlow: Flow<PagingAction>,
        scope: CoroutineScope,
    ): Flow<PagingData<List<TopicModel<Torrent>>>> {
        return filterFlow.flatMapLatest { filter ->
            PagingDataLoader(
                fetchData = { page -> searchService.search(filter, page) },
                transform = { torrents -> enrichTopicsUseCase(torrents) },
                actions = actionsFlow.onStart { refresh() },
                scope = scope,
                logger = loggerFactory.get("SearchPagingDataLoader"),
            ).flow
        }
    }
}

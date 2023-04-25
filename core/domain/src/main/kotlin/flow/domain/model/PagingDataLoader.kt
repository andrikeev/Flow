package flow.domain.model

import flow.logger.api.Logger
import flow.models.LoadState
import flow.models.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class PagingDataLoader<Item : Any, Data>(
    private val fetchData: suspend (Int) -> Page<Item>,
    transform: suspend (List<Item>) -> Flow<Data>,
    actions: Flow<PagingAction>,
    scope: CoroutineScope,
    private val logger: Logger,
) {
    private val items = MutableStateFlow<List<Item>?>(null)
    private val loadStates = MutableStateFlow(LoadStates.Idle)
    private val pagination = MutableStateFlow(Pagination())

    val flow: Flow<PagingData<Data>> = combine(
        items.flatMapLatest { it?.let { transform(it) } ?: flowOf(null) },
        loadStates,
        pagination,
        ::PagingData,
    )

    init {
        scope.launch {
            actions.collectLatest { action ->
                when (action) {
                    is PagingAction.Append -> append()
                    is PagingAction.Prepend -> prepend()
                    is PagingAction.Refresh -> refresh(action.page)
                    is PagingAction.Retry -> retry()
                }
            }
        }
    }

    private suspend fun refresh(start: Int) {
        logger.d { "refresh: start=$start" }
        runCatching {
            items.clear()
            loadStates.refresh()
            pagination.start(start)
            fetchData(start)
        }.onSuccess(::updateItems).onFailure(::onFailure)
    }

    private suspend fun append() {
        if (!loadStates.isAppend()) {
            logger.d { "append" }
            val lastPage = pagination.lastPage
            if (lastPage < pagination.totalPages) {
                runCatching {
                    loadStates.append()
                    fetchData(lastPage + 1)
                }.onSuccess(::updateItems).onFailure(::onFailure)
            }
        }
    }

    private suspend fun prepend() {
        if (!loadStates.isPrepend()) {
            logger.d { "prepend" }
            val firstPage = pagination.firstPage
            if (firstPage > 1) {
                runCatching {
                    loadStates.prepend()
                    fetchData(firstPage - 1)
                }.onSuccess(::updateItems).onFailure(::onFailure)
            }
        }
    }

    private suspend fun retry() {
        logger.d { "retry" }
        when {
            loadStates.isRefreshError() -> refresh(pagination.firstPage)
            loadStates.isAppendError() -> append()
            loadStates.isPrependError() -> prepend()
        }
    }

    private fun updateItems(update: Page<Item>) {
        logger.d { "updateItems: items=$update" }
        when {
            loadStates.isRefresh() -> {
                items.set(update.items)
                pagination.set(update.page, update.pages)
            }

            loadStates.isAppend() -> {
                items.append(update.items)
                pagination.append(update.page)
            }

            loadStates.isPrepend() -> {
                items.prepend(update.items)
                pagination.prepend(update.page)
            }
        }
        loadStates.idle()
    }

    private fun onFailure(error: Throwable) {
        logger.e(error) { "onFailure: loadStates=${loadStates.value}" }
        when {
            loadStates.isRefresh() -> loadStates.refreshError(error)
            loadStates.isAppend() -> loadStates.appendError(error)
            loadStates.isPrepend() -> loadStates.prependError(error)
        }
    }

    companion object {
        private val StateFlow<Pagination>.firstPage: Int
            get() = value.loadedPages.first

        private val StateFlow<Pagination>.lastPage: Int
            get() = value.loadedPages.last

        private val StateFlow<Pagination>.totalPages: Int
            get() = value.totalPages

        private fun MutableStateFlow<Pagination>.start(page: Int) {
            value = Pagination(page..page, 0)
        }

        private fun MutableStateFlow<Pagination>.set(page: Int, pages: Int) {
            value = Pagination(page..page, pages)
        }

        private fun MutableStateFlow<Pagination>.append(page: Int) {
            value = value.copy(loadedPages = firstPage..page)
        }

        private fun MutableStateFlow<Pagination>.prepend(page: Int) {
            value = value.copy(loadedPages = page..lastPage)
        }

        private fun <Item> MutableStateFlow<List<Item>?>.clear() {
            value = null
        }

        private fun <Item> MutableStateFlow<List<Item>?>.set(items: List<Item>) {
            value = items
        }

        private fun <Item> MutableStateFlow<List<Item>?>.append(items: List<Item>) {
            value = value.orEmpty() + items
        }

        private fun <Item> MutableStateFlow<List<Item>?>.prepend(items: List<Item>) {
            value = items + value.orEmpty()
        }

        private fun MutableStateFlow<LoadStates>.refresh() {
            value = LoadStates(refresh = LoadState.Loading)
        }

        private fun MutableStateFlow<LoadStates>.refreshError(error: Throwable) {
            value = LoadStates(refresh = LoadState.Error(error))
        }

        private fun MutableStateFlow<LoadStates>.isRefresh(): Boolean {
            return value.refresh == LoadState.Loading
        }

        private fun MutableStateFlow<LoadStates>.isRefreshError(): Boolean {
            return value.refresh is LoadState.Error
        }

        private fun MutableStateFlow<LoadStates>.append() {
            value = LoadStates(append = LoadState.Loading)
        }

        private fun MutableStateFlow<LoadStates>.appendError(error: Throwable) {
            value = LoadStates(append = LoadState.Error(error))
        }

        private fun MutableStateFlow<LoadStates>.isAppend(): Boolean {
            return value.append == LoadState.Loading
        }

        private fun MutableStateFlow<LoadStates>.isAppendError(): Boolean {
            return value.append is LoadState.Error
        }

        private fun MutableStateFlow<LoadStates>.prepend() {
            value = LoadStates(prepend = LoadState.Loading)
        }

        private fun MutableStateFlow<LoadStates>.prependError(error: Throwable) {
            value = LoadStates(prepend = LoadState.Error(error))
        }

        private fun MutableStateFlow<LoadStates>.isPrepend(): Boolean {
            return value.prepend == LoadState.Loading
        }

        private fun MutableStateFlow<LoadStates>.isPrependError(): Boolean {
            return value.prepend is LoadState.Error
        }

        private fun MutableStateFlow<LoadStates>.idle() {
            value = LoadStates.Idle
        }
    }
}

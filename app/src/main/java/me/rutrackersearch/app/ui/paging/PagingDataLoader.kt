package me.rutrackersearch.app.ui.paging

import androidx.paging.CombinedLoadStates
import androidx.paging.DifferCallback
import androidx.paging.NullPaddedList
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataDiffer
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.rutrackersearch.domain.entity.Page

class PagingDataLoader<T : Any>(
    initialPage: Int = 1,
    pageSize: Int,
    fetchData: suspend (Int) -> Page<T>,
    actions: Flow<PagingAction>,
    scope: CoroutineScope,
) {
    private val pagingDataDiffer = object : PagingDataDiffer<T>(
        object : DifferCallback {
            override fun onChanged(position: Int, count: Int) {
                if (count > 0) {
                    updateItemSnapshotList()
                }
            }

            override fun onInserted(position: Int, count: Int) {
                if (count > 0) {
                    updateItemSnapshotList()
                }
            }

            override fun onRemoved(position: Int, count: Int) {
                if (count > 0) {
                    updateItemSnapshotList()
                }
            }
        }
    ) {
        override suspend fun presentNewList(
            previousList: NullPaddedList<T>,
            newList: NullPaddedList<T>,
            lastAccessedIndex: Int,
            onListPresentable: () -> Unit
        ): Int? {
            onListPresentable()
            updateItemSnapshotList()
            return null
        }
    }

    private val mutableItems = MutableStateFlow(emptyList<T>())

    init {
        scope.launch {
            Pager(
                PagingConfig(
                    pageSize = pageSize,
                    prefetchDistance = pageSize,
                    enablePlaceholders = false,
                    initialLoadSize = pageSize,
                )
            ) {
                object : PagingSource<Int, T>() {
                    override fun getRefreshKey(state: PagingState<Int, T>): Int = 1

                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
                        val nextPage = params.key ?: initialPage
                        return kotlin.runCatching { fetchData(nextPage) }
                            .fold(
                                { data ->
                                    LoadResult.Page(
                                        data = data.items,
                                        prevKey = null,
                                        nextKey = if (data.page == data.pages) null else data.page + 1
                                    )
                                },
                                {
                                    LoadResult.Error(it)
                                }
                            )
                    }
                }
            }.flow.collectLatest { pagingData ->
                pagingDataDiffer.collectFrom(pagingData)
            }
        }
        scope.launch {
            actions.collectLatest { action ->
                when (action) {
                    PagingAction.Refresh -> pagingDataDiffer.refresh()
                    PagingAction.Retry -> pagingDataDiffer.retry()
                    PagingAction.Append -> if (pagingDataDiffer.size != 0) {
                        pagingDataDiffer[pagingDataDiffer.size - 1]
                    }
                    PagingAction.Prepend -> if (pagingDataDiffer.size != 0) {
                        pagingDataDiffer[0]
                    }
                }
            }
        }
    }

    val flow: Flow<PagingData<T>> = combine(
        mutableItems,
        pagingDataDiffer.loadStateFlow.map(::toLoadStates),
        ::PagingData,
    )

    private fun updateItemSnapshotList() {
        mutableItems.value = pagingDataDiffer.snapshot().items
    }

    private fun toLoadStates(combinedLoadStates: CombinedLoadStates): LoadStates {
        return LoadStates(
            refresh = toLoadState(combinedLoadStates.refresh),
            prepend = toLoadState(combinedLoadStates.prepend),
            append = toLoadState(combinedLoadStates.append),
        )
    }

    private fun toLoadState(loadState: androidx.paging.LoadState): LoadState {
        return when (loadState) {
            is androidx.paging.LoadState.NotLoading -> LoadState.NotLoading
            androidx.paging.LoadState.Loading -> LoadState.Loading
            is androidx.paging.LoadState.Error -> LoadState.Error(loadState.error)
        }
    }
}

package flow.domain.model

import kotlinx.coroutines.flow.FlowCollector

sealed interface PagingAction {
    data class Refresh(val page: Int = 1) : PagingAction
    object Prepend : PagingAction
    object Append : PagingAction
    object Retry : PagingAction
}

suspend fun FlowCollector<PagingAction>.refresh(page: Int = 1) {
    emit(PagingAction.Refresh(page))
}

suspend fun FlowCollector<PagingAction>.append() {
    emit(PagingAction.Append)
}

suspend fun FlowCollector<PagingAction>.prepend() {
    emit(PagingAction.Prepend)
}

suspend fun FlowCollector<PagingAction>.retry() {
    emit(PagingAction.Retry)
}

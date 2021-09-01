package me.rutrackersearch.app.ui.paging

sealed interface PagingAction {
    object Refresh : PagingAction
    object Prepend : PagingAction
    object Append : PagingAction
    object Retry : PagingAction
}

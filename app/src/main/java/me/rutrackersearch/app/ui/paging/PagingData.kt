package me.rutrackersearch.app.ui.paging

data class PagingData<T>(
    val items: List<T>,
    val loadStates: LoadStates,
)

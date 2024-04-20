package flow.domain.model.search

import flow.models.search.Search

data class SearchHistory(
    val pinned: List<Search>,
    val other: List<Search>,
)

fun SearchHistory.isEmpty() = pinned.isEmpty() && other.isEmpty()

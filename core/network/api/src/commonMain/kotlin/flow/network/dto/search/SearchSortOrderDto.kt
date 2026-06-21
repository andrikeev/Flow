package flow.network.dto.search

import kotlinx.serialization.Serializable

@Serializable
enum class SearchSortOrderDto(val value: String) {
    Ascending("1"),
    Descending("2"),
}

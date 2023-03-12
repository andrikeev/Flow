package flow.network.dto.search

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
enum class SearchSortOrderDto(@Transient val value: String) {
    ASCENDING("1"),
    DESCENDING("2"),
}

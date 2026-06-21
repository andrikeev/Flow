package flow.network.dto.search

import kotlinx.serialization.Serializable

@Serializable
enum class SearchSortTypeDto(val value: String) {
    Date("1"),
    Title("2"),
    Downloaded("4"),
    Seeds("10"),
    Leeches("11"),
    Size("7"),
}

package flow.network.dto.search

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
enum class SearchSortTypeDto(@Transient val value: String) {
    DATE("1"),
    TITLE("2"),
    DOWNLOADED("4"),
    SEEDS("10"),
    LEECHES("11"),
    SIZE("7"),
}

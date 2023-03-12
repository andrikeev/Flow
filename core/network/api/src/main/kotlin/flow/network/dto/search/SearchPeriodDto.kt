package flow.network.dto.search

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
enum class SearchPeriodDto(@Transient val value: String) {
    ALL_TIME("-1"),
    TODAY("1"),
    LAST_THREE_DAYS("3"),
    LAST_WEEK("7"),
    LAST_TWO_WEEKS("14"),
    LAST_MONTH("32"),
}

package flow.network.dto.search

import kotlinx.serialization.Serializable

@Serializable
enum class SearchPeriodDto(val value: String) {
    AllTime("-1"),
    Today("1"),
    LastThreeDays("3"),
    LastWeek("7"),
    LastTwoWeeks("14"),
    LastMonth("32"),
}

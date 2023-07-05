package flow.data.converters

import flow.database.entity.SearchHistoryEntity
import flow.database.entity.SuggestEntity
import flow.models.Page
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Search
import flow.models.search.Sort
import flow.models.topic.Torrent
import flow.network.dto.search.SearchPageDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import flow.network.dto.topic.TorrentDto

internal fun SearchPageDto.toSearchPage(): Page<Torrent> {
    return Page(
        page = page,
        pages = pages,
        items = torrents.map(TorrentDto::toTorrent),
    )
}

internal fun Period.toDto(): SearchPeriodDto = when (this) {
    Period.ALL_TIME -> SearchPeriodDto.AllTime
    Period.TODAY -> SearchPeriodDto.Today
    Period.LAST_THREE_DAYS -> SearchPeriodDto.LastThreeDays
    Period.LAST_WEEK -> SearchPeriodDto.LastWeek
    Period.LAST_TWO_WEEKS -> SearchPeriodDto.LastTwoWeeks
    Period.LAST_MONTH -> SearchPeriodDto.LastMonth
}

internal fun Sort.toDto(): SearchSortTypeDto = when (this) {
    Sort.DATE -> SearchSortTypeDto.Date
    Sort.TITLE -> SearchSortTypeDto.Title
    Sort.DOWNLOADED -> SearchSortTypeDto.Downloaded
    Sort.SEEDS -> SearchSortTypeDto.Seeds
    Sort.LEECHES -> SearchSortTypeDto.Leeches
    Sort.SIZE -> SearchSortTypeDto.Size
}

internal fun Order.toDto(): SearchSortOrderDto = when (this) {
    Order.ASCENDING -> SearchSortOrderDto.Ascending
    Order.DESCENDING -> SearchSortOrderDto.Descending
}

internal fun SearchHistoryEntity.toSearch(): Search {
    return Search(
        id = id,
        filter = Filter(
            query = query,
            sort = sort,
            order = order,
            period = period,
            author = author,
            categories = categories,
        ),
    )
}

internal fun Filter.toEntity(): SearchHistoryEntity {
    return SearchHistoryEntity(
        id = id(),
        timestamp = System.currentTimeMillis(),
        query = query,
        sort = sort,
        order = order,
        period = period,
        author = author,
        categories = categories,
    )
}

private fun Filter.id(): Int {
    var id = query?.hashCode() ?: 0
    id = 31 * id + period.ordinal
    id = 31 * id + (author?.id?.hashCode() ?: 0)
    id = 31 * id + (categories?.sumOf(Category::hashCode) ?: 0)
    return id
}

internal fun String.toEntity(): SuggestEntity {
    return SuggestEntity(
        id = lowercase().hashCode(),
        timestamp = System.currentTimeMillis(),
        suggest = this,
    )
}

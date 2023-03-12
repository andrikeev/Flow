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
import flow.network.dto.ResultDto
import flow.network.dto.search.SearchPageDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import flow.network.dto.topic.TorrentDto

internal fun ResultDto<SearchPageDto>.toSearchPage(): Page<Torrent> {
    require(this is ResultDto.Data)
    return Page(
        page = value.page,
        pages = value.pages,
        items = value.torrents.map(TorrentDto::toTorrent),
    )
}

internal fun Period.toDto(): SearchPeriodDto = when (this) {
    Period.ALL_TIME -> SearchPeriodDto.ALL_TIME
    Period.TODAY -> SearchPeriodDto.TODAY
    Period.LAST_THREE_DAYS -> SearchPeriodDto.LAST_THREE_DAYS
    Period.LAST_WEEK -> SearchPeriodDto.LAST_WEEK
    Period.LAST_TWO_WEEKS -> SearchPeriodDto.LAST_TWO_WEEKS
    Period.LAST_MONTH -> SearchPeriodDto.LAST_MONTH
}

internal fun Sort.toDto(): SearchSortTypeDto = when (this) {
    Sort.DATE -> SearchSortTypeDto.DATE
    Sort.TITLE -> SearchSortTypeDto.TITLE
    Sort.DOWNLOADED -> SearchSortTypeDto.DOWNLOADED
    Sort.SEEDS -> SearchSortTypeDto.SEEDS
    Sort.LEECHES -> SearchSortTypeDto.LEECHES
    Sort.SIZE -> SearchSortTypeDto.SIZE
}

internal fun Order.toDto(): SearchSortOrderDto = when (this) {
    Order.ASCENDING -> SearchSortOrderDto.ASCENDING
    Order.DESCENDING -> SearchSortOrderDto.DESCENDING
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
        )
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

package me.rutrackersearch.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Search
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.models.topic.Author

@Entity(tableName = "Search")
data class SearchHistoryEntity(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val query: String?,
    val sort: Sort,
    val order: Order,
    val period: Period,
    val author: Author?,
    val categories: List<Category>?,
) {
    fun toSearch(): Search {
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

    companion object {
        fun of(filter: Filter): SearchHistoryEntity {
            return SearchHistoryEntity(
                id = filter.id(),
                timestamp = System.currentTimeMillis(),
                query = filter.query,
                sort = filter.sort,
                order = filter.order,
                period = filter.period,
                author = filter.author,
                categories = filter.categories,
            )
        }

        private fun Filter.id(): Int {
            var id = query?.hashCode() ?: 0
            id = 31 * id + period.ordinal
            id = 31 * id + (author?.id?.hashCode() ?: 0)
            id = 31 * id + (categories?.sumOf(Category::hashCode) ?: 0)
            return id
        }
    }
}

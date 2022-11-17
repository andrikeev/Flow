package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Search
import flow.models.search.Sort
import flow.models.topic.Author

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

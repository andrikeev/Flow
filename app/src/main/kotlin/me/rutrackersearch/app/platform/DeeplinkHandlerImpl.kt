package me.rutrackersearch.app.platform

import android.net.Uri
import androidx.navigation.NavHostController
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.ui.platform.DeeplinkHandler
import me.rutrackersearch.app.navigation.openCategory
import me.rutrackersearch.app.navigation.openSearchResult
import me.rutrackersearch.app.navigation.openTopic

class DeeplinkHandlerImpl(
    private val navController: NavHostController
) : DeeplinkHandler {
    override fun handle(uri: Uri): Boolean {
        return if (
            uri.host?.contains("rutracker.org") == false &&
            uri.host?.contains("rutracker.net") == false
        ) {
            return false
        } else {
            uri.path?.let { path ->
                when {
                    path.contains("viewtopic.php") -> {
                        val id = uri.getQueryParameters("t")
                        val pid = uri.getQueryParameters("p")
                        if (!id.isNullOrEmpty() || !pid.isNullOrEmpty()) {
                            navController.openTopic(id = id.firstOrNull(), pid = pid.firstOrNull())
                            true
                        } else {
                            false
                        }
                    }

                    path.contains("viewforum.php") -> {
                        uri.getQueryParameters("f")?.firstOrNull()?.let { id ->
                            navController.openCategory(Category(id, ""))
                            true
                        } ?: false
                    }

                    path.contains("tracker.php") -> {
                        val query = uri.getQueryParameters("nm").firstOrNull()
                        val categories = uri.getQueryParameters("f")?.let {
                            it.map { id -> Category(id, "") }
                        } ?: emptyList()
                        val authorName = uri.getQueryParameters("pn").firstOrNull().orEmpty()
                        val authorId = uri.getQueryParameters("pid").firstOrNull()
                        val author = Author(id = authorId, name = authorName)
                        val sortType = uri.getQueryParameters("o").firstOrNull()?.let {
                            when (it) {
                                "1" -> Sort.DATE
                                "2" -> Sort.TITLE
                                "4" -> Sort.DOWNLOADED
                                "10" -> Sort.SEEDS
                                "11" -> Sort.LEECHES
                                "7" -> Sort.SIZE
                                else -> null
                            }
                        } ?: Sort.DATE
                        val sortOrder = uri.getQueryParameters("s").firstOrNull()?.let {
                            when (it) {
                                "1" -> Order.ASCENDING
                                "2" -> Order.DESCENDING
                                else -> null
                            }
                        } ?: Order.ASCENDING
                        val period = uri.getQueryParameters("tm").firstOrNull()?.let {
                            when (it) {
                                "-1" -> Period.ALL_TIME
                                "1" -> Period.TODAY
                                "3" -> Period.LAST_THREE_DAYS
                                "7" -> Period.LAST_WEEK
                                "14" -> Period.LAST_TWO_WEEKS
                                "32" -> Period.LAST_MONTH
                                else -> null
                            }
                        } ?: Period.ALL_TIME
                        val filter = Filter(
                            query = query,
                            sort = sortType,
                            order = sortOrder,
                            period = period,
                            author = author,
                            categories = categories,
                        )
                        navController.openSearchResult(filter)
                        true
                    }

                    else -> false
                }
            } ?: false
        }
    }
}

package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.*
import io.ktor.server.routing.*

internal fun Application.configureSearchRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/search") {
            respond(
                api.getSearchPage(
                    token = call.request.authToken,
                    searchQuery = call.request.queryParameters["query"],
                    categories = call.request.queryParameters["categories"],
                    author = call.request.queryParameters["author"],
                    authorId = call.request.queryParameters["authorId"],
                    sortType = call.request.queryParameters["sort"]?.toEnumOrNull<SearchSortTypeDto>(),
                    sortOrder = call.request.queryParameters["order"]?.toEnumOrNull<SearchSortOrderDto>(),
                    period = call.request.queryParameters["period"]?.toEnumOrNull<SearchPeriodDto>(),
                    page = call.request.queryParameters["page"]?.toIntOrNull(),
                )
            )
        }
    }
}

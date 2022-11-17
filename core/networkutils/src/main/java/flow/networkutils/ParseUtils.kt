package flow.networkutils

import java.net.URI

fun getIdFromUrl(url: String?, key: String): String? {
    return url?.let { parseUrl(url)[key]?.firstOrNull() }
}

fun requireIdFromUrl(url: String, key: String): String {
    return requireNotNull(parseUrl(url)[key]?.firstOrNull()) { "query param not found in url" }
}

private fun parseUrl(url: String): Map<String, List<String>> {
    return try {
        URI.create(url)
            .query.split("&")
            .associate { queryParam ->
                val split = queryParam.split("=")
                split[0] to split.drop(1)
            }
    } catch (e: Exception) {
        emptyMap()
    }
}

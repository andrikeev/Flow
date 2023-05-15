package flow.network.domain

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URI
import java.util.regex.Pattern
import kotlin.math.ln
import kotlin.math.pow

internal fun Element?.toInt(default: Int = 0): Int {
    return this?.text()?.toIntOrNull() ?: default
}

internal fun Element?.toStr(): String {
    return this?.text().orEmpty()
}

internal fun Element?.urlOrNull(): String? {
    return this?.attr("href")
}

internal fun Element?.url(): String {
    return requireNotNull(urlOrNull()) { "url not found in $this" }
}

internal fun Element?.queryParamOrNull(key: String): String? {
    return urlOrNull()?.let(::parseUrl)?.get(key)?.firstOrNull()
}

internal fun Element?.queryParam(key: String): String {
    return requireNotNull(queryParamOrNull(key)) { "query param not found in $this" }
}

internal fun Elements?.toIntOrNull(): Int? {
    return this?.text()?.toIntOrNull()
}

internal fun Elements?.toInt(default: Int = 0): Int {
    return this?.toIntOrNull() ?: default
}

internal fun Elements?.toStrOrNull(): String? {
    return this?.text()
}

internal fun Elements?.toStr(): String {
    return this?.text().orEmpty()
}

internal fun Elements?.urlOrNull(): String? {
    return this?.attr("href")
}

internal fun Elements?.url(): String {
    return requireNotNull(urlOrNull()) { "url not found in $this" }
}

internal fun Elements?.queryParamOrNull(key: String): String? {
    return urlOrNull()?.let(::parseUrl)?.get(key)?.firstOrNull()
}

internal fun Elements?.queryParam(key: String): String {
    return requireNotNull(queryParamOrNull(key)) { "query param not found in $this" }
}

internal fun isTopicExists(html: String): Boolean {
    return !html.contains("Тема не найдена") &&
            !html.contains("Тема находится в мусорке") &&
            !html.contains("Ошибочный запрос: не указан topic_id")
}

internal fun isBlockedForRegion(html: String): Boolean {
    return html.contains("Извините, раздача недоступна для вашего региона")
}

internal fun isTopicModerated(html: String): Boolean {
    return html.contains("Раздача ожидает проверки модератором")
}

internal fun getTitle(titleWithTags: String): String {
    return titleWithTags
        .replace("\\[[^]]*]".toRegex(), "")
        .replace("  ", " ")
        .replace("[", "")
        .replace("]", "")
        .trim()
}

internal fun getTags(titleWithTags: String): String {
    val stringBuilder = StringBuilder()
    val matcher = Pattern.compile("(\\[[^]]*])").matcher(titleWithTags)
    while (matcher.find()) stringBuilder.append(matcher.group(1), ' ')
    return stringBuilder.toString()
}

internal fun formatSize(sizeBytes: Long): String {
    if (sizeBytes < 1024) {
        return "$sizeBytes B"
    }
    val exp = (ln(sizeBytes.toDouble()) / ln(1024.0)).toInt()
    val pre = "KMGTPE"[exp - 1].toString()
    return String.format("%.1f %sB", sizeBytes / 1024.0.pow(exp.toDouble()), pre)
}

private fun parseUrl(url: String) = runCatching {
    URI.create(url)
        .query.split("&")
        .associate { queryParam ->
            val split = queryParam.split("=")
            split[0] to split.drop(1)
        }
}.getOrDefault(emptyMap())

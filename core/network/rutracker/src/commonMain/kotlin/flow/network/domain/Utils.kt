package flow.network.domain

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

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
    Regex("(\\[[^]]*])").findAll(titleWithTags).forEach { match ->
        stringBuilder.append(match.groupValues[1]).append(' ')
    }
    return stringBuilder.toString()
}

internal fun formatSize(sizeBytes: Long): String {
    if (sizeBytes < 1024) {
        return "$sizeBytes B"
    }
    val exp = (ln(sizeBytes.toDouble()) / ln(1024.0)).toInt()
    val pre = "KMGTPE"[exp - 1].toString()
    val value = sizeBytes / 1024.0.pow(exp.toDouble())
    // Mimics "%.1f" formatting without the JVM-only String.format.
    val scaled = (value * 10).roundToInt()
    return "${scaled / 10}.${scaled % 10} ${pre}B"
}

private fun parseUrl(url: String): Map<String, List<String>> = runCatching {
    url.substringAfter('?', "")
        .split("&")
        .filter(String::isNotEmpty)
        .associate { queryParam ->
            val split = queryParam.split("=")
            split[0] to split.drop(1)
        }
}.getOrDefault(emptyMap())

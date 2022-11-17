package flow.network.parsers

import java.util.regex.Matcher
import java.util.regex.Pattern.compile
import kotlin.math.ln
import kotlin.math.pow

internal fun isAuthorisedPage(html: String): Boolean {
    return html.contains("logged-in-username")
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

internal fun getTitle(titleWithTags: String): String =
    titleWithTags.replace("\\[[^]]*]".toRegex(), "").replace("  ", " ").trim()

internal fun getTags(titleWithTags: String): String {
    val stringBuilder = StringBuilder()
    val matcher = compile("(\\[[^]]*])").matcher(titleWithTags)
    while (matcher.find()) stringBuilder.append(matcher.group(1))
    return stringBuilder.toString()
}

private val formTokenMatcher = compile("form_token: '(.*)',")
internal fun parseFormToken(html: String): String =
    formTokenMatcher.matcher(html).takeIf(Matcher::find)?.group(1).orEmpty()

internal fun formatSize(sizeBytes: Long): String {
    if (sizeBytes < 1024) {
        return "$sizeBytes B"
    }
    val exp = (ln(sizeBytes.toDouble()) / ln(1024.0)).toInt()
    val pre = "KMGTPE"[exp - 1].toString()
    return String.format("%.1f %sB", sizeBytes / 1024.0.pow(exp.toDouble()), pre)
}


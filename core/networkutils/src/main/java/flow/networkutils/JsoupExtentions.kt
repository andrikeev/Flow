package flow.networkutils

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Element?.toInt(default: Int = 0): Int = this?.text()?.toIntOrNull() ?: default
fun Element?.toStr(): String = this?.text().orEmpty()
fun Element?.toStrOrNull(): String? = this?.text()
fun Element?.url(): String = requireNotNull(this.urlOrNull()) { "url not found" }
fun Element?.urlOrNull(): String? = this?.attr("href")

fun Elements?.toInt(default: Int = 0): Int = this?.text()?.toIntOrNull() ?: default
fun Elements?.toIntOrNull(): Int? = this?.text()?.toIntOrNull()
fun Elements?.toStrOrNull(): String? = this?.text()
fun Elements?.toStr(): String = this?.text().orEmpty()
fun Elements?.urlOrNull(): String? = this?.attr("href")
fun Elements?.url(): String = requireNotNull(this.urlOrNull()) { "url not found" }

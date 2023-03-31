package flow.network.domain

import flow.network.model.Unauthorized
import java.util.regex.Pattern

internal object WithFormTokenUseCase {

    suspend operator fun <T> invoke(
        html: String,
        block: suspend (formToken: String) -> T,
    ): T {
        val formToken = parseFormToken(html)
        return if (formToken.isEmpty()) {
            throw Unauthorized
        } else {
            block(formToken)
        }
    }

    private val formTokenRegex = Pattern.compile("form_token: '(.*)',")

    private fun parseFormToken(html: String): String {
        val matcher = formTokenRegex.matcher(html)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            ""
        }
    }
}

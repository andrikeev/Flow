package flow.network.domain

import flow.network.model.Unauthorized

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

    private val formTokenRegex = Regex("form_token: '(.*)',")

    private fun parseFormToken(html: String): String {
        return formTokenRegex.find(html)?.groupValues?.get(1) ?: ""
    }
}

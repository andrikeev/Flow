package flow.network.domain

import flow.network.dto.ResultDto
import flow.network.dto.error.FlowError
import java.util.regex.Pattern

internal object WithFormTokenUseCase {

    suspend operator fun <T> invoke(
        html: String,
        block: suspend (formToken: String) -> ResultDto<T>,
    ) = tryCatching {
        val formToken = parseFormToken(html)
        if (formToken.isEmpty()) {
            ResultDto.Error(FlowError.Unauthorized)
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

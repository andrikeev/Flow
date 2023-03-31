package flow.navigation.model

data class NavigationArgument(
    val name: String,
    val nullable: Boolean = false,
)

context(NavigationGraphBuilder)
fun buildRoute(
    route: String,
    requiredArgsBuilder: StringBuilder.() -> Unit = {},
    optionalArgsBuilder: StringBuilder.() -> Unit = {},
) = buildString {
    graph?.let { append(it, '/') }
    append(route)
    requiredArgsBuilder()
    optionalArgsBuilder()
}

fun buildDeepLink(
    url: String,
    queryParamsBuilder: StringBuilder.() -> Unit = {},
) = buildString {
    append(url)
    queryParamsBuilder()
}

fun StringBuilder.appendRequiredArgs(vararg args: String) {
    args.forEach { argumentName -> append('/', '{', argumentName, '}') }
}

fun StringBuilder.appendOptionalArgs(vararg args: String) {
    if (args.isNotEmpty()) {
        append('?')
    }
    args.forEachIndexed { index, argumentName ->
        append("$argumentName={$argumentName}")
        if (index != args.lastIndex) {
            append('&')
        }
    }
}

fun StringBuilder.appendRequiredParams(vararg params: Any) {
    params.forEach { value -> append('/', value) }
}

fun StringBuilder.appendOptionalParams(vararg params: Pair<String, Any?>) {
    val initial = length
    params.forEach { (name, value) ->
        val current = length
        if (value != null) {
            append(if (current == initial) '?' else '&')
            append(name, '=', value)
        }
    }
}

package flow.models.search

data class Suggest(
    val value: String,
    val substring: IntRange? = null,
)

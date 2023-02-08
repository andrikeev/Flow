package flow.securestorage.model

data class Account(
    val id: String,
    val name: String,
    val password: String,
    val token: String,
    val avatarUrl: String?,
)

package me.rutrackersearch.auth.models

data class AccountData(
    val id: String,
    val token: String,
    val avatarUrl: String?,
)
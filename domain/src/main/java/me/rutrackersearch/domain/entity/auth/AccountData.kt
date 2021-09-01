package me.rutrackersearch.domain.entity.auth

data class AccountData(
    val id: String,
    val token: String,
    val avatarUrl: String?,
)

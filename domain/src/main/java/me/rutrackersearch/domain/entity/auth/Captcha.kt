package me.rutrackersearch.domain.entity.auth

data class Captcha(
    val id: String,
    val code: String,
    val url: String,
)

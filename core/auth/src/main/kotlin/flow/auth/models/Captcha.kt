package flow.auth.models

data class Captcha(
    val id: String,
    val code: String,
    val url: String,
)
package me.rutrackersearch.data.converters

import me.rutrackersearch.domain.entity.auth.AccountData
import me.rutrackersearch.domain.entity.auth.AuthResponse
import me.rutrackersearch.domain.entity.auth.Captcha
import me.rutrackersearch.domain.entity.error.Failure
import org.json.JSONObject

fun JSONObject.parseAuthResponse(): AuthResponse {
    return when (get("status")) {
        "OK" -> {
            AuthResponse.Success(getJSONObject("user").parseUserData())
        }
        "CAPTCHA" -> {
            AuthResponse.CaptchaRequired(getJSONObject("captcha").parseCaptcha())
        }
        "WRONG_CREDITS" -> {
            AuthResponse.WrongCredits(optJSONObject("captcha")?.parseCaptcha())
        }
        else -> {
            AuthResponse.Error(Failure.ParseError())
        }
    }
}

private fun JSONObject.parseCaptcha(): Captcha {
    return Captcha(
        id = getString("id"),
        code = getString("code"),
        url = getString("url"),
    )
}

private fun JSONObject.parseUserData(): AccountData {
    return AccountData(
        id = getString("id"),
        token = getString("token"),
        avatarUrl = optString("avatarUrl"),
    )
}

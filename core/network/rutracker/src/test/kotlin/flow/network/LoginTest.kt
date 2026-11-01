package flow.network

import flow.network.domain.GetCurrentProfileUseCase
import flow.network.domain.GetProfileUseCase
import flow.network.domain.LoginUseCase
import flow.network.dto.auth.AuthResponseDto
import flow.network.impl.RuTrackerInnerApiImpl
import flow.network.model.NoData
import flow.network.model.Unknown
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginTest {
    @Test
    fun `session cookie is selected by name in either header order`() =
        runTest {
            val session = "bb_session=test-session; Path=/forum/; HttpOnly; Secure"
            val other = "bb_tracking=tracking; Path=/"
            for (cookies in listOf(listOf(other, session), listOf(session, other))) {
                withClient(cookies) { api ->
                    assertEquals("bb_session=test-session", api.login("user", "pass", null, null, null).first)
                }
            }
        }

    @Test
    fun `unrelated malformed empty and expired cookies are not sessions`() =
        runTest {
            for (cookie in listOf(
                "bb_tracking=tracking",
                "bb_ssl=1",
                "not a cookie",
                "bb_session=; Path=/",
                "bb_session=deleted; Max-Age=0",
                "bb_session=deleted; Expires=Thu, 01 Jan 1970 00:00:00 GMT",
            )) {
                withClient(listOf(cookie)) { api ->
                    assertNull(api.login("user", "pass", null, null, null).first)
                }
            }
        }

    @Test
    fun `session on redirect is retained and sent without Set-Cookie attributes`() =
        runTest {
            withClient(
                cookies = listOf("bb_tracking=tracking", "bb_session=test-session; Path=/; HttpOnly"),
                status = HttpStatusCode.Found,
            ) { api ->
                val result = loginUseCase(api)("user", "pass", null, null, null)
                assertTrue(result is AuthResponseDto.Success)
                assertEquals("42", (result as AuthResponseDto.Success).user.id)
                assertEquals("bb_session=test-session", result.user.token)
                // Closing the temporary login client must not close the original client.
                assertTrue(api.mainPage(result.user.token).contains("logged-in-username"))
            }
        }

    @Test
    fun `captcha is recognized despite service cookie markup and relative URL changes`() =
        runTest {
            for ((src, expected) in listOf(
                "/captcha/image.png" to "https://rutracker.org/captcha/image.png",
                "images/captcha/image.png" to "https://rutracker.org/forum/images/captcha/image.png",
                "//static.example/captcha/image.png" to "https://static.example/captcha/image.png",
                "https://static.example/captcha/image.png?a=1&amp;b=2" to
                    "https://static.example/captcha/image.png?a=1&b=2",
            )) {
                withClient(listOf("bb_tracking=tracking"), captcha(src)) { api ->
                    val result = loginUseCase(api)("user", "pass", null, null, null)
                    assertTrue(result is AuthResponseDto.CaptchaRequired)
                    val parsed = (result as AuthResponseDto.CaptchaRequired).captcha!!
                    assertEquals("sid", parsed.id)
                    assertEquals("cap_code_dynamic", parsed.code)
                    assertEquals(expected, parsed.url)
                }
            }
        }

    @Test
    fun `wrong password is recognized with and without captcha`() =
        runTest {
            for (html in listOf("", captcha("/captcha/image.png"))) {
                withClient(emptyList(), "<p>Неверный <b>пароль</b></p>$html") { api ->
                    val result = loginUseCase(api)("user", "pass", null, null, null)
                    assertTrue(result is AuthResponseDto.WrongCredits)
                    assertEquals(html.isNotEmpty(), (result as AuthResponseDto.WrongCredits).captcha != null)
                }
            }
        }

    @Test
    fun `unknown form and unrelated page remain errors`() =
        runTest {
            for ((html, error) in listOf(
                "<form><input name='login_username'></form>" to Unknown,
                "<p>Service unavailable</p>" to NoData,
            )) {
                withClient(emptyList(), html) { api ->
                    val actual = runCatching { loginUseCase(api)("user", "pass", null, null, null) }.exceptionOrNull()
                    assertEquals(error, actual)
                }
            }
        }

    @Test
    fun `incomplete captcha is an error rather than successful login`() =
        runTest {
            withClient(emptyList(), "<form id='login-form'><input name='cap_sid' value='sid'></form>") { api ->
                val error = runCatching { loginUseCase(api)("user", "pass", null, null, null) }.exceptionOrNull()
                assertEquals(Unknown, error)
            }
        }

    @Test
    fun `session without authenticated page does not produce success`() =
        runTest {
            val client =
                HttpClient(
                    MockEngine { request ->
                        if (request.url.encodedPath.endsWith("login.php")) {
                            respond("", headers = headersOf(HttpHeaders.SetCookie, "bb_session=test-session"))
                        } else {
                            respond("<form id='login-form'></form>")
                        }
                    },
                ) {
                    defaultRequest { url("https://rutracker.org/forum/") }
                }
            try {
                val error =
                    runCatching {
                        loginUseCase(RuTrackerInnerApiImpl(client))("user", "pass", null, null, null)
                    }.exceptionOrNull()
                assertTrue(error is IllegalArgumentException)
            } finally {
                client.close()
            }
        }

    private fun captcha(src: String) =
        """
        <form>
            <input value='sid'
                   type='hidden' name='cap_sid'>
            <input type='text' name='cap_code_dynamic'>
            <img src='$src'>
        </form>
        """.trimIndent()

    private fun loginUseCase(api: RuTrackerInnerApiImpl) =
        LoginUseCase(
            api,
            GetCurrentProfileUseCase(api, GetProfileUseCase(api)),
        )

    private suspend fun withClient(
        cookies: List<String>,
        html: String = "",
        status: HttpStatusCode = HttpStatusCode.OK,
        block: suspend (RuTrackerInnerApiImpl) -> Unit,
    ) {
        val client =
            HttpClient(
                MockEngine { request ->
                    when (request.url.encodedPath) {
                        "/forum/login.php" ->
                            respond(
                                html,
                                status,
                                headersOf(
                                    HttpHeaders.SetCookie to cookies,
                                    HttpHeaders.Location to listOf("index.php"),
                                ),
                            )
                        "/forum/index.php" -> {
                            assertEquals("bb_session=test-session", request.headers[HttpHeaders.Cookie])
                            respond("<a id='logged-in-username' href='profile.php?u=42'>User</a>")
                        }
                        "/forum/profile.php" -> respond("<span id='profile-uname' data-uid='42'>User</span>")
                        else -> error("Unexpected request: ${request.url}")
                    }
                },
            ) {
                defaultRequest { url("https://rutracker.org/forum/") }
            }
        try {
            block(RuTrackerInnerApiImpl(client))
        } finally {
            client.close()
        }
    }
}

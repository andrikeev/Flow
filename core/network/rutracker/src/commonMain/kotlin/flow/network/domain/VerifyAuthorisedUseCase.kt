package flow.network.domain

internal object VerifyAuthorisedUseCase {
    operator fun invoke(html: String): Boolean {
        return html.contains("logged-in-username")
    }
}

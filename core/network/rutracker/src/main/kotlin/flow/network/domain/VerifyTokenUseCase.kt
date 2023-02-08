package flow.network.domain

internal object VerifyTokenUseCase {
    operator fun invoke(token: String): Boolean {
        return token.isNotEmpty()
    }
}

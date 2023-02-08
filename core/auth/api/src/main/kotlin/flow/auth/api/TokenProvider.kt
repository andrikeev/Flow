package flow.auth.api

interface TokenProvider {
    fun getToken(): String
    suspend fun refreshToken(): Boolean
}

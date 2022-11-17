package flow.domain.usecase

import flow.auth.api.AuthRepository
import flow.data.api.FavoritesRepository
import flow.models.user.Account
import javax.inject.Inject

class SaveAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(account: Account) {
        authRepository.saveAccount(account)
        favoritesRepository.loadFavorites()
    }
}

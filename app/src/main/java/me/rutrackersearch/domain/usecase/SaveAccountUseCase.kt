package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.AccountRepository
import me.rutrackersearch.domain.service.LoadFavoritesService
import me.rutrackersearch.models.user.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val loadFavoritesService: LoadFavoritesService,
) {
    suspend operator fun invoke(account: Account) {
        accountRepository.saveAccount(account)
        loadFavoritesService.start()
    }
}

package flow.domain.usecase

import flow.data.api.repository.ForumRepository
import flow.data.api.service.ForumService
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshForumUseCase @Inject constructor(
    private val forumRepository: ForumRepository,
    private val forumService: ForumService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            forumRepository.storeForum(forumService.getForum())
        }
    }
}

package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.auth.api.AuthRepository
import flow.database.dao.FavoriteTopicDao
import flow.database.entity.FavoriteTopicEntity
import flow.models.topic.BaseTopic
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.network.NetworkApi
import kotlinx.coroutines.coroutineScope

@HiltWorker
internal class LoadFavoritesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkApi: NetworkApi,
    private val authRepository: AuthRepository,
    private val favoriteTopicDao: FavoriteTopicDao,
    private val notificationService: flow.notifications.NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        if (authRepository.isAuthorized()) {
            runCatching {
                val remoteFavoriteTopics = loadRemoteFavorites()
                val remoteFavoriteIds = remoteFavoriteTopics.map(Topic::id).toSet()
                val localFavoriteIds = favoriteTopicDao.getAllIds()

                val toAdd = remoteFavoriteTopics.map(FavoriteTopicEntity.Companion::of)
                val toDelete = localFavoriteIds.subtract(remoteFavoriteIds)

                favoriteTopicDao.deleteByIds(toDelete)
                favoriteTopicDao.insertAll(toAdd)
            }.getOrElse {
                retryOrFailure()
            }
        }
        success()
    }

    private suspend fun loadRemoteFavorites(): List<Topic> {
        //TODO: Try to optimize parallel work
        return with(networkApi.favorites()) {
            items
                .plus(
                    IntRange(page + 1, pages)
                        .map { page -> networkApi.favorites(page) }
                        .flatMap { page -> page.items }
                )
                .map { topic ->
                    when (topic) {
                        is BaseTopic -> topic
                        is Torrent -> networkApi.torrent(topic.id)
                    }
                }
        }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}

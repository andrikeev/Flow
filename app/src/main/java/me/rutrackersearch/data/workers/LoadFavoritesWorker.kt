package me.rutrackersearch.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import me.rutrackersearch.auth.AuthObservable
import me.rutrackersearch.data.database.dao.FavoriteTopicDao
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.network.NetworkApi
import me.rutrackersearch.notification.NotificationService

@HiltWorker
class LoadFavoritesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: NetworkApi,
    private val authObservable: AuthObservable,
    private val dao: FavoriteTopicDao,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        if (authObservable.authorised) {
            runCatching {
                val firstPage = api.favorites()
                val remoteFavoriteTopics = firstPage.items + IntRange(
                    2,
                    firstPage.pages
                ).map { page -> api.favorites(page) }.flatMap { page -> page.items }
                val remoteFavorites = remoteFavoriteTopics.map(Topic::id).toSet()
                val localFavorites = dao.getAllIds()

                val toAdd = remoteFavoriteTopics.filterNot { topic -> topic.id in localFavorites }
                    .map { topic ->
                        when (topic) {
                            is BaseTopic -> topic
                            is Torrent -> api.torrent(topic.id)
                        }
                    }.map { FavoriteTopicEntity.of(it) }
                val toDelete = localFavorites.subtract(remoteFavorites)

                dao.deleteByIds(toDelete)
                dao.insertAll(toAdd)
                Result.success()
            }.getOrElse {
                Result.retry()
            }
        } else {
            Result.success()
        }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}

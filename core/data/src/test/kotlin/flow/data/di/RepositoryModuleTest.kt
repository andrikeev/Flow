package flow.data.di

import flow.database.dao.BookmarkDao
import flow.database.dao.FavoriteSearchDao
import flow.database.dao.FavoriteTopicDao
import flow.database.dao.ForumCategoryDao
import flow.database.dao.ForumMetadataDao
import flow.database.dao.SearchHistoryDao
import flow.database.dao.SuggestDao
import flow.database.dao.VisitedTopicDao
import flow.logger.api.LoggerFactory
import flow.securestorage.PreferencesStorage
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class RepositoryModuleTest {

    /**
     * Statically validates that every repository's constructor dependencies are
     * resolvable. The external dependencies (DAOs, PreferencesStorage, LoggerFactory)
     * are declared as extra types since they are provided by other Koin modules.
     */
    @Test
    fun repositoryGraphIsComplete() {
        repositoryModule.verify(
            extraTypes = listOf(
                BookmarkDao::class,
                FavoriteSearchDao::class,
                FavoriteTopicDao::class,
                ForumCategoryDao::class,
                ForumMetadataDao::class,
                SearchHistoryDao::class,
                SuggestDao::class,
                VisitedTopicDao::class,
                PreferencesStorage::class,
                LoggerFactory::class,
            ),
        )
    }
}

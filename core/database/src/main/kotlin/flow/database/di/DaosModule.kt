/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flow.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.database.AppDatabase
import flow.database.dao.BookmarkDao
import flow.database.dao.FavoriteTopicDao
import flow.database.dao.ForumCategoryDao
import flow.database.dao.ForumMetadataDao
import flow.database.dao.SearchHistoryDao
import flow.database.dao.SuggestDao
import flow.database.dao.VisitedTopicDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    @Singleton
    fun providesBookmarkDao(appDatabase: AppDatabase): BookmarkDao = appDatabase.bookmarkDao()

    @Provides
    @Singleton
    fun providesFavoriteTopicDao(appDatabase: AppDatabase): FavoriteTopicDao = appDatabase.favoriteTopicDao()

    @Provides
    @Singleton
    fun providesForumCategoryDao(appDatabase: AppDatabase): ForumCategoryDao = appDatabase.forumCategoryDao()

    @Provides
    @Singleton
    fun providesForumMetadataDao(appDatabase: AppDatabase): ForumMetadataDao = appDatabase.forumMetadataDao()

    @Provides
    @Singleton
    fun providesVisitedTopicDao(appDatabase: AppDatabase): VisitedTopicDao = appDatabase.visitedTopicDao()

    @Provides
    @Singleton
    fun providesSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao = appDatabase.searchHistoryDao()

    @Provides
    @Singleton
    fun providesSuggestDao(appDatabase: AppDatabase): SuggestDao = appDatabase.suggestDao()
}

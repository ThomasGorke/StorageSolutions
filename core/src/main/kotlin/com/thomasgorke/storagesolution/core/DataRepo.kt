/*
 * Copyright 2020 Tailored Media GmbH.
 * Created by Florian Schuster.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thomasgorke.storagesolution.core

import com.thomasgorke.storagesolution.core.StorageType.*
import com.thomasgorke.storagesolution.core.local.FileStorage
import com.thomasgorke.storagesolution.core.local.SpStorage
import com.thomasgorke.storagesolution.core.local.firebase.FirebaseStorage
import com.thomasgorke.storagesolution.core.local.room.RoomDatabase
import com.thomasgorke.storagesolution.core.local.sql.SqlDatabase
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.FirebaseAuthor
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.utils.*
import java.util.*

interface DataRepo {
    suspend fun getAllAuthors(storageType: StorageType): List<Author>
    suspend fun insertAuthor(storageType: StorageType, newAuthor: Author): Author
    suspend fun deleteAuthor(storageType: StorageType, authorId: String)

    suspend fun getAllNewsByAuthorId(storageType: StorageType, authorId: String): List<News>
    suspend fun insertNews(storageType: StorageType, news: News, authorId: String): News
    suspend fun updateNews(storageType: StorageType, news: News): News
    suspend fun deleteNews(storageType: StorageType, newsId: String)
}

class CoreDataRepo(
    private val spStorage: SpStorage,
    private val sqlDatabase: SqlDatabase,
    private val fileStorage: FileStorage,
    private val roomDatabase: RoomDatabase,
    private val firebaseStorage: FirebaseStorage
) : DataRepo {

    override suspend fun getAllAuthors(storageType: StorageType): List<Author> {
        return when (storageType) {
            SHARED_PREFERENCES -> spStorage.getAllAuthors().map { it.toAuthor() }
            FILE -> emptyList()
            SQL -> sqlDatabase.getAllAuthors().map { it.toAuthor() }
            ROOM -> roomDatabase.getAllAuthors().map { it.toAuthor() }
            FIREBASE -> emptyList()
        }
    }

    override suspend fun insertAuthor(storageType: StorageType, newAuthor: Author): Author {
        newAuthor.apply { id = UUID.randomUUID().toString() }

        when (storageType) {
            SHARED_PREFERENCES -> spStorage.insertAuthor(newAuthor.toPreference()).id
            FILE -> {
            }
            SQL -> sqlDatabase.insertAuthor(newAuthor.toSqlEntity()).id
            ROOM -> roomDatabase.insertAuthor(newAuthor.toRoomEntity()).id
            FIREBASE -> {
                firebaseStorage.insertAuthor(FirebaseAuthor(newAuthor.name, "www.google.com"))
            }
        }

        return newAuthor
    }

    override suspend fun deleteAuthor(storageType: StorageType, authorId: String) {
        when (storageType) {
            SHARED_PREFERENCES -> spStorage.deleteAuthor(authorId)
            FILE -> {
            }
            SQL -> {
            }
            ROOM -> {
            }
            FIREBASE -> {
            }
        }
    }

    override suspend fun getAllNewsByAuthorId(
        storageType: StorageType,
        authorId: String
    ): List<News> {
        return when (storageType) {
            SHARED_PREFERENCES -> spStorage.getNewsByAuthorId(authorId).map { it.toNews() }
            FILE -> emptyList()
            SQL -> sqlDatabase.getNewsByAuthorId(authorId).map { it.toNews() }
            ROOM -> roomDatabase.getNewsByAuthorId(authorId).map { it.toNews() }
            FIREBASE -> emptyList()
        }
    }

    override suspend fun insertNews(
        storageType: StorageType,
        news: News,
        authorId: String
    ): News {
        news.apply { id = UUID.randomUUID().toString() }

        when (storageType) {
            SHARED_PREFERENCES -> spStorage.insertNews(news.toPreference(authorId))
            FILE -> {
            }
            SQL -> sqlDatabase.insertNews(news.toSqlEntity(authorId))
            ROOM -> roomDatabase.insertNews(news.toRoomEntity(authorId))
            FIREBASE -> {
            }
        }

        return news
    }

    override suspend fun updateNews(storageType: StorageType, news: News): News {
        return when (storageType) {
            SHARED_PREFERENCES -> spStorage.updateNews(news.toPreference()).toNews()
            FILE -> news
            SQL -> news
            ROOM -> news
            FIREBASE -> news
        }
    }

    override suspend fun deleteNews(storageType: StorageType, newsId: String) {
        when (storageType) {
            SHARED_PREFERENCES -> spStorage.deleteNews(newsId)
            FILE -> {
            }
            SQL -> {
            }
            ROOM -> {
            }
            FIREBASE -> {
            }
        }
    }
}

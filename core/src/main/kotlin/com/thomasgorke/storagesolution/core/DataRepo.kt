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
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.utils.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface DataRepo {
    suspend fun getAllAuthors(storageType: StorageType): List<Author>
    suspend fun insertAuthor(storageType: StorageType, newAuthor: Author): Author
    suspend fun deleteAuthor(storageType: StorageType, authorId: String)

    suspend fun getAllNewsByAuthorId(storageType: StorageType, authorId: String): List<News>
    suspend fun insertNews(storageType: StorageType, news: News): News
    suspend fun updateNews(storageType: StorageType, news: News): News
    suspend fun deleteNews(storageType: StorageType, news: News)
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
            FIREBASE -> firebaseStorage.getAllAuthors()
        }
    }

    override suspend fun insertAuthor(storageType: StorageType, newAuthor: Author): Author {
        newAuthor.apply { id = UUID.randomUUID().toString() }

        when (storageType) {
            SHARED_PREFERENCES -> spStorage.insertAuthor(newAuthor.toPreference())
            FILE -> {
            }
            SQL -> sqlDatabase.insertAuthor(newAuthor.toSqlEntity())
            ROOM -> roomDatabase.insertAuthor(newAuthor.toRoomEntity())
            FIREBASE -> firebaseStorage.insertAuthor(newAuthor)
        }

        return newAuthor
    }

    override suspend fun deleteAuthor(storageType: StorageType, authorId: String) {
        when (storageType) {
            SHARED_PREFERENCES -> spStorage.deleteAuthor(authorId)
            FILE -> {
            }
            SQL -> sqlDatabase.deleteAuthor(authorId)
            ROOM -> roomDatabase.deleteAuthor(authorId)
            FIREBASE -> firebaseStorage.deleteAuthor(authorId)
        }
    }

    override suspend fun getAllNewsByAuthorId(
        storageType: StorageType,
        authorId: String
    ): List<News> {
        return when (storageType) {
            SHARED_PREFERENCES -> spStorage.getNewsByAuthorId(authorId).map { it.toNews(authorId) }
            FILE -> emptyList()
            SQL -> sqlDatabase.getNewsByAuthorId(authorId).map { it.toNews(authorId) }
            ROOM -> roomDatabase.getNewsByAuthorId(authorId).map { it.toNews(authorId) }
            FIREBASE -> firebaseStorage.getAllNewsByAuthorId(authorId)
        }
    }

    override suspend fun insertNews(storageType: StorageType, news: News): News {
        news.apply { id = UUID.randomUUID().toString() }

        when (storageType) {
            SHARED_PREFERENCES -> spStorage.insertNews(news.toPreference())
            FILE -> {
            }
            SQL -> sqlDatabase.insertNews(news.toSqlEntity())
            ROOM -> roomDatabase.insertNews(news.toRoomEntity())
            FIREBASE -> firebaseStorage.insertNews(news)
        }

        return news
    }

    override suspend fun updateNews(storageType: StorageType, news: News): News {
        return when (storageType) {
            SHARED_PREFERENCES -> spStorage.updateNews(news.toPreference()).toNews(news.authorId)
            FILE -> news
            SQL -> sqlDatabase.updateNews(news.toSqlEntity()).toNews(news.authorId)
            ROOM -> roomDatabase.updateNews(news.toRoomEntity()).toNews(news.authorId)
            FIREBASE -> firebaseStorage.updateNews(news)
        }
    }

    override suspend fun deleteNews(storageType: StorageType, news: News) {
        when (storageType) {
            SHARED_PREFERENCES -> spStorage.deleteNews(news.id)
            FILE -> {
            }
            SQL -> sqlDatabase.deleteNews(news.id)
            ROOM -> roomDatabase.deleteNews(news.id)
            FIREBASE -> firebaseStorage.deleteNews(news)
        }
    }
}

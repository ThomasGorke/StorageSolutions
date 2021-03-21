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

import android.graphics.BitmapFactory
import coil.request.ImageRequest
import com.thomasgorke.storagesolution.core.StorageType.*
import com.thomasgorke.storagesolution.core.local.FileStorage
import com.thomasgorke.storagesolution.core.local.SpStorage
import com.thomasgorke.storagesolution.core.local.firebase.FirebaseStorage
import com.thomasgorke.storagesolution.core.local.sql.SqlDatabase
import com.thomasgorke.storagesolution.core.local.room.RoomDatabase
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.FirebaseAuthor
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.utils.*
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

interface DataRepo {
    suspend fun getAllAuthors(storageType: StorageType): List<Author>
    suspend fun insertAuthor(storageType: StorageType, newAuthor: Author): Author

    suspend fun getAllNewsByAuthorId(storageType: StorageType, id: Long): List<News>
    suspend fun insertNews(storageType: StorageType, news: News, authorId: Long): News
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
            SHARED_PREFERENCES -> spStorage.getAllAuthors().map { it.toAuthor().apply { this.id = it.id } }
            FILE -> emptyList()
            SQL -> sqlDatabase.getAllAuthors().map { it.toAuthor().apply { this.id = it.id } }
            ROOM -> roomDatabase.getAllAuthors().map { it.toAuthor().apply { this.id = it.id } }
            FIREBASE -> emptyList()
        }
    }

    override suspend fun insertAuthor(storageType: StorageType, newAuthor: Author): Author {
        val newId: Long = when (storageType) {
            SHARED_PREFERENCES -> spStorage.insertAuthor(newAuthor.toPreference()).id
            FILE -> 0
            SQL -> sqlDatabase.insertAuthor(newAuthor.toSqlEntity()).id
            ROOM -> roomDatabase.insertAuthor(newAuthor.toRoomEntity()).id
            FIREBASE -> {
                firebaseStorage.insertAuthor(FirebaseAuthor(newAuthor.name, "www.google.com"))
                0
            }
        }

        return newAuthor.apply { id = newId }
    }

    override suspend fun getAllNewsByAuthorId(storageType: StorageType, id: Long): List<News> {
        return when (storageType) {
            SHARED_PREFERENCES -> spStorage.getNewsByAuthorId(id).map { it.toNews().apply { this.id = it.id } }
            FILE -> emptyList()
            SQL -> sqlDatabase.getNewsByAuthorId(id).map { it.toNews().apply { this.id = it.id } }
            ROOM -> roomDatabase.getNewsByAuthorId(id).map { it.toNews().apply { this.id = it.id } }
            FIREBASE -> emptyList()
        }
    }

    override suspend fun insertNews(storageType: StorageType, news: News, authorId: Long): News {
        val newId: Long = when (storageType) {
            SHARED_PREFERENCES -> spStorage.insertNews(news.toPreference(authorId)).id
            FILE -> 0
            SQL -> sqlDatabase.insertNews(news.toSqlEntity(authorId)).id
            ROOM -> roomDatabase.insertNews(news.toRoomEntity(authorId)).id
            FIREBASE -> 0
        }

        return news.apply { this.id = newId }
    }
}

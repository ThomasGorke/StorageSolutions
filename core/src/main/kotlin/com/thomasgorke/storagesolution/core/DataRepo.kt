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

import com.thomasgorke.storagesolution.core.local.FileStorage
import com.thomasgorke.storagesolution.core.local.room.RoomDatabase
import com.thomasgorke.storagesolution.core.local.SpStorage
import com.thomasgorke.storagesolution.core.local.SqlDatabase
import com.thomasgorke.storagesolution.core.model.Author

interface DataRepo {
    fun getAllAuthors(storageType: StorageType): List<Author>
    fun insertAuthor(storageType: StorageType, newAuthor: Author): Author
}

class CoreDataRepo(
    private val spStorage: SpStorage,
    private val sqlDatabase: SqlDatabase,
    private val fileStorage: FileStorage,
    private val roomDatabase: RoomDatabase
) : DataRepo {

    override fun getAllAuthors(storageType: StorageType): List<Author> {
        return emptyList()
    }

    override fun insertAuthor(storageType: StorageType, newAuthor: Author): Author {
        return newAuthor
    }
}
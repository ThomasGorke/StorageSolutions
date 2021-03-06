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

package com.thomasgorke.storagesolution.core.local

import android.content.Context
import androidx.room.Room
import com.thomasgorke.storagesolution.core.local.file.FileStorage
import com.thomasgorke.storagesolution.core.local.file.FileStorageImpl
import com.thomasgorke.storagesolution.core.local.firebase.FirebaseStorage
import com.thomasgorke.storagesolution.core.local.firebase.FirebaseStorageImpl
import com.thomasgorke.storagesolution.core.local.prefs.SpStorage
import com.thomasgorke.storagesolution.core.local.prefs.SpStorageImpl
import com.thomasgorke.storagesolution.core.local.room.RoomAppDatabase
import com.thomasgorke.storagesolution.core.local.room.RoomDatabase
import com.thomasgorke.storagesolution.core.local.room.RoomDatabaseImpl
import com.thomasgorke.storagesolution.core.local.sql.SqlDatabase
import com.thomasgorke.storagesolution.core.local.sql.SqlDatabaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


private const val STORAGE_SOLUTION_PREFS = "STORAGE_SOLUTION_PREFS"

internal val localModule = module {
    // Preferences
    single { androidContext().getSharedPreferences(STORAGE_SOLUTION_PREFS, Context.MODE_PRIVATE) }
    single<SpStorage> { SpStorageImpl(prefs = get(), gson = get()) }

    //Sql
    single<SqlDatabase> {
        SqlDatabaseImpl(
            androidContext()
        )
    }

    //File
    single<FileStorage> { FileStorageImpl(context = androidContext(), gson = get()) }

    //Room
    single {
        Room.databaseBuilder(androidContext(), RoomAppDatabase::class.java, "app-database").build()
    }
    single<RoomDatabase> { RoomDatabaseImpl(roomAppDatabase = get()) }

    //Firebase
    single<FirebaseStorage> {
        FirebaseStorageImpl(
            imageReference = get(),
            firebaseStore = get()
        )
    }
}



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
import com.thomasgorke.storagesolution.core.local.room.RoomAppDatabase
import com.thomasgorke.storagesolution.core.local.room.RoomDatabase
import com.thomasgorke.storagesolution.core.local.room.RoomDatabaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


private const val STORAGE_SOLUTION_PREFS = "STORAGE_SOLUTION_PREFS"

internal val localModule = module {
    single { androidContext().getSharedPreferences(STORAGE_SOLUTION_PREFS, Context.MODE_PRIVATE) }
    single { Room.databaseBuilder(androidContext(), RoomAppDatabase::class.java, "app-database").build() }


    single<SpStorage> { SpStorageImpl(prefs = get(), gson = get()) }
    single<SqlDatabase> { SqlDatabaseImpl(androidContext()) }
    single<FileStorage> { FileStorageImpl() }
    single<RoomDatabase> {
        RoomDatabaseImpl(
            roomAppDatabase = get()
        )
    }
}



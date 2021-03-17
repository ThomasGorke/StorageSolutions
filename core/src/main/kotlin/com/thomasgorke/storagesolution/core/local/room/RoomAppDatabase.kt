package com.thomasgorke.storagesolution.core.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thomasgorke.storagesolution.core.model.room.AuthorEntity
import com.thomasgorke.storagesolution.core.model.room.NewsEntity

@Database(
    entities = [AuthorEntity::class, NewsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RoomAppDatabase: RoomDatabase() {
    abstract fun appDatabaseDao(): RoomDbDao
}
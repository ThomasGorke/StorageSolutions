package com.thomasgorke.storagesolution.core.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thomasgorke.storagesolution.core.model.room.RoomAuthorEntity
import com.thomasgorke.storagesolution.core.model.room.RoomNewsEntity

@Database(
    entities = [RoomAuthorEntity::class, RoomNewsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RoomAppDatabase: RoomDatabase() {
    abstract fun appDatabaseDao(): RoomDbDao
}
package com.thomasgorke.storagesolution.core.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thomasgorke.storagesolution.core.model.room.RoomAuthorEntity
import com.thomasgorke.storagesolution.core.model.room.RoomNewsEntity

@Dao
interface RoomDbDao {
    @Query("SELECT * FROM t_author ORDER BY name")
    suspend fun getAllAuthors(): List<RoomAuthorEntity>

    @Query("SELECT * FROM t_news WHERE authorId = :authorId ORDER BY title")
    suspend fun getAllNewsByAuthorId(authorId: String): List<RoomNewsEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: RoomAuthorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(new: RoomNewsEntity): Long
}
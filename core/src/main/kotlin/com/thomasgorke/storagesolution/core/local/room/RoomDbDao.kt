package com.thomasgorke.storagesolution.core.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thomasgorke.storagesolution.core.model.room.AuthorEntity
import com.thomasgorke.storagesolution.core.model.room.NewsEntity

@Dao
interface RoomDbDao {
    @Query("SELECT * FROM t_author ORDER BY name")
    suspend fun getAllAuthors(): List<AuthorEntity>

    @Query("SELECT * FROM t_news WHERE authorId = :authorId ORDER BY title")
    suspend fun getAllNewsByAuthorId(authorId: Long): List<NewsEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(new: NewsEntity): Long
}
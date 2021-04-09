package com.thomasgorke.storagesolution.core.local.room

import androidx.room.*
import com.thomasgorke.storagesolution.core.model.room.RoomAuthorEntity
import com.thomasgorke.storagesolution.core.model.room.RoomNewsEntity

@Dao
interface RoomDbDao {
    // SELECT
    @Query("SELECT * FROM t_author ORDER BY name")
    suspend fun getAllAuthors(): List<RoomAuthorEntity>

    @Query("SELECT * FROM t_news WHERE authorId = :authorId ORDER BY title")
    suspend fun getAllNewsByAuthorId(authorId: String): List<RoomNewsEntity>

    // INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: RoomAuthorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(new: RoomNewsEntity): Long

    // UPDATE
    @Update(entity = RoomNewsEntity::class)
    suspend fun updateNews(news: RoomNewsEntity)

    // DELETE
    @Query("DELETE FROM t_author WHERE id = :authorId")
    suspend fun deleteAuthorById(authorId: String)

    @Query("DELETE FROM t_news WHERE id = :newsId")
    suspend fun deleteNewsById(newsId: String)

    @Query("DELETE FROM t_news WHERE authorId = :authorId")
    suspend fun deleteNewsByAuthorId(authorId: String)
}
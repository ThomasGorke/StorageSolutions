package com.thomasgorke.storagesolution.core.local.room

import com.thomasgorke.storagesolution.core.model.room.RoomAuthorEntity
import com.thomasgorke.storagesolution.core.model.room.RoomNewsEntity


interface RoomDatabase {
    suspend fun insertAuthor(newAuthor: RoomAuthorEntity): RoomAuthorEntity
    suspend fun getAllAuthors(): List<RoomAuthorEntity>

    suspend fun insertNews(news: RoomNewsEntity): RoomNewsEntity
    suspend fun getNewsByAuthorId(authorId: String): List<RoomNewsEntity>
}

class RoomDatabaseImpl(
    private val roomAppDatabase: RoomAppDatabase
) : RoomDatabase {

    override suspend fun insertAuthor(newAuthor: RoomAuthorEntity): RoomAuthorEntity {
        val generatedId = roomAppDatabase.appDatabaseDao()
            .insertAuthor(newAuthor)
        return newAuthor
    }

    override suspend fun getAllAuthors(): List<RoomAuthorEntity> {
        return roomAppDatabase.appDatabaseDao().getAllAuthors()
    }

    override suspend fun insertNews(news: RoomNewsEntity): RoomNewsEntity {
        val generatedId = roomAppDatabase.appDatabaseDao()
            .insertNews(news)

        return news
    }

    override suspend fun getNewsByAuthorId(authorId: String): List<RoomNewsEntity> {
        return roomAppDatabase.appDatabaseDao().getAllNewsByAuthorId(authorId)
    }
}
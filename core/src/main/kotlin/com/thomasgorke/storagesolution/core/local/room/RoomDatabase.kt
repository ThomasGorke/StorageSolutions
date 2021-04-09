package com.thomasgorke.storagesolution.core.local.room

import com.thomasgorke.storagesolution.core.model.room.RoomAuthorEntity
import com.thomasgorke.storagesolution.core.model.room.RoomNewsEntity


interface RoomDatabase {
    suspend fun insertAuthor(newAuthor: RoomAuthorEntity): RoomAuthorEntity
    suspend fun getAllAuthors(): List<RoomAuthorEntity>
    suspend fun deleteAuthor(authorId: String)

    suspend fun insertNews(news: RoomNewsEntity): RoomNewsEntity
    suspend fun getNewsByAuthorId(authorId: String): List<RoomNewsEntity>
    suspend fun updateNews(news: RoomNewsEntity): RoomNewsEntity
    suspend fun deleteNews(newsId: String)
}

class RoomDatabaseImpl(
    private val roomAppDatabase: RoomAppDatabase
) : RoomDatabase {

    override suspend fun insertAuthor(newAuthor: RoomAuthorEntity): RoomAuthorEntity {
        roomAppDatabase.appDatabaseDao()
            .insertAuthor(newAuthor)
        return newAuthor
    }

    override suspend fun getAllAuthors(): List<RoomAuthorEntity> {
        return roomAppDatabase.appDatabaseDao().getAllAuthors()
    }

    override suspend fun deleteAuthor(authorId: String) {
        roomAppDatabase.appDatabaseDao().deleteAuthorById(authorId)
        roomAppDatabase.appDatabaseDao().deleteNewsByAuthorId(authorId)
    }


    override suspend fun insertNews(news: RoomNewsEntity): RoomNewsEntity {
        roomAppDatabase.appDatabaseDao().insertNews(news)
        return news
    }

    override suspend fun getNewsByAuthorId(authorId: String): List<RoomNewsEntity> {
        return roomAppDatabase.appDatabaseDao().getAllNewsByAuthorId(authorId)
    }

    override suspend fun updateNews(news: RoomNewsEntity): RoomNewsEntity {
        roomAppDatabase.appDatabaseDao().updateNews(news)
        return news
    }

    override suspend fun deleteNews(newsId: String) {
        roomAppDatabase.appDatabaseDao().deleteNewsById(newsId)
    }
}
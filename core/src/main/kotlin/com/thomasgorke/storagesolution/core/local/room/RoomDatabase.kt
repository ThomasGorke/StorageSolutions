package com.thomasgorke.storagesolution.core.local.room

import com.thomasgorke.storagesolution.core.model.room.AuthorEntity
import com.thomasgorke.storagesolution.core.model.room.NewsEntity


interface RoomDatabase {
    suspend fun insertAuthor(newAuthor: AuthorEntity): AuthorEntity
    suspend fun getAllAuthors(): List<AuthorEntity>

    suspend fun insertNews(news: NewsEntity): NewsEntity
    suspend fun getNewsByAuthorId(authorId: Long): List<NewsEntity>
}

class RoomDatabaseImpl(
    private val roomAppDatabase: RoomAppDatabase
) : RoomDatabase {

    override suspend fun insertAuthor(newAuthor: AuthorEntity): AuthorEntity {
        val generatedId = roomAppDatabase.appDatabaseDao()
            .insertAuthor(AuthorEntity(newAuthor.name, newAuthor.image))
        return newAuthor.apply { id = generatedId }
    }

    override suspend fun getAllAuthors(): List<AuthorEntity> {
        return roomAppDatabase.appDatabaseDao().getAllAuthors()
    }

    override suspend fun insertNews(news: NewsEntity): NewsEntity {
        val generatedId = roomAppDatabase.appDatabaseDao()
            .insertNews(NewsEntity(news.title, news.content, news.authorId))

        return news.apply { id = generatedId }
    }

    override suspend fun getNewsByAuthorId(authorId: Long): List<NewsEntity> {
        return roomAppDatabase.appDatabaseDao().getAllNewsByAuthorId(authorId)
    }
}
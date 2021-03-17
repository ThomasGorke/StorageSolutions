package com.thomasgorke.storagesolution.core.local.room

import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.model.room.AuthorEntity
import com.thomasgorke.storagesolution.core.model.room.NewsEntity
import com.thomasgorke.storagesolution.core.utils.toBitmap
import com.thomasgorke.storagesolution.core.utils.toByteArray


interface RoomDatabase {
    suspend fun insertAuthor(newAuthor: Author): Author
    suspend fun getAllAuthors(): List<Author>

    suspend fun insertNews(news: News): News
    suspend fun getNewsByAuthorId(authorId: Long): List<News>
}

class RoomDatabaseImpl(
    private val roomAppDatabase: RoomAppDatabase
) : RoomDatabase {

    override suspend fun insertAuthor(newAuthor: Author): Author {
        val generatedId = roomAppDatabase.appDatabaseDao()
            .insertAuthor(AuthorEntity(newAuthor.name, newAuthor.image.toByteArray()))
        return newAuthor.apply { id = generatedId }
    }

    override suspend fun getAllAuthors(): List<Author> {
        return roomAppDatabase.appDatabaseDao().getAllAuthors()
            .map { Author(it.name, it.image.toBitmap(), getNewsByAuthorId(it.id)) }
    }

    override suspend fun insertNews(news: News): News {
        val generatedId = roomAppDatabase.appDatabaseDao()
            .insertNews(NewsEntity(news.headline, news.content, news.authorId))
        return news.apply { id = generatedId }
    }

    override suspend fun getNewsByAuthorId(authorId: Long): List<News> {
        return roomAppDatabase.appDatabaseDao().getAllNewsByAuthorId(authorId)
            .map { News(it.title, it.content, it.authorId) }
    }
}
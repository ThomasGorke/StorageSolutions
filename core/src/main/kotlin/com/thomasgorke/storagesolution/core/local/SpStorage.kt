package com.thomasgorke.storagesolution.core.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.News

interface SpStorage {
    suspend fun insertAuthor(newAuthor: Author): Author
    suspend fun getAllAuthors(): List<Author>

    suspend fun insertNews(news: News): News
    suspend fun getNewsByAuthorId(authorId: Long): List<News>
}

class SpStorageImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson
) : SpStorage {

    private val editor = prefs.edit()

    private var nextAuthorId = 0L
    private var nextNewsId = 0L

    override suspend fun insertAuthor(newAuthor: Author): Author {
        val actualAuthors = getAllAuthors().toMutableList()
        actualAuthors.add(newAuthor.apply { nextAuthorId++ })

        editor.putString(AUTHORS, gson.toJson(actualAuthors)).apply()
        return newAuthor
    }

    override suspend fun getAllAuthors(): List<Author> {
        val jsonAuthors = prefs.getString(AUTHORS, "") ?: return emptyList()
        return gson.fromJson(jsonAuthors, Array<Author>::class.java).toList()
    }

    override suspend fun insertNews(news: News): News {
        val actualNews = getNewsByAuthorId(news.authorId).toMutableList()
        actualNews.add(news.apply { id = nextNewsId++ })

        editor.putString(NEWS, gson.toJson(actualNews)).apply()
        return news
    }

    override suspend fun getNewsByAuthorId(authorId: Long): List<News> {
        val jsonNews = prefs.getString(NEWS, "") ?: return emptyList()
        return gson.fromJson(jsonNews, Array<News>::class.java).toList().filter { it.authorId == authorId }
    }

    companion object {
        private const val AUTHORS = "authors"
        private const val NEWS = "news"
    }
}

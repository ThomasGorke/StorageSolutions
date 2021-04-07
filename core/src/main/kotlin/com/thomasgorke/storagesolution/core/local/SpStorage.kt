package com.thomasgorke.storagesolution.core.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.thomasgorke.storagesolution.core.model.prefs.AuthorPreference
import com.thomasgorke.storagesolution.core.model.prefs.NewsPreference
import com.thomasgorke.storagesolution.core.utils.updateById

interface SpStorage {
    suspend fun insertAuthor(newAuthor: AuthorPreference): AuthorPreference
    suspend fun getAllAuthors(): List<AuthorPreference>
    suspend fun deleteAuthor(authorId: String)

    suspend fun insertNews(news: NewsPreference): NewsPreference
    suspend fun getNewsByAuthorId(authorId: String): List<NewsPreference>
    suspend fun updateNews(news: NewsPreference): NewsPreference
    suspend fun deleteNews(newsId: String)
}

class SpStorageImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson
) : SpStorage {

    private val editor = prefs.edit()

    override suspend fun insertAuthor(newAuthor: AuthorPreference): AuthorPreference {
        val actualAuthors = getAllAuthors().toMutableList()
        actualAuthors.add(newAuthor)

        editor.putString(AUTHORS, gson.toJson(actualAuthors)).commit()
        return newAuthor
    }

    override suspend fun getAllAuthors(): List<AuthorPreference> {
        val jsonAuthors = prefs.getString(AUTHORS, "")
        if (jsonAuthors == null || jsonAuthors.isEmpty()) return emptyList()

        return gson.fromJson(jsonAuthors, Array<AuthorPreference>::class.java).toList()
    }

    override suspend fun deleteAuthor(authorId: String) {
        // delete author
        val authors = getAllAuthors().filter { it.id != authorId }
        editor.putString(AUTHORS, gson.toJson(authors)).commit()

        // delete news
        val news = getAllNews().filter { it.authorId != authorId }
        editor.putString(NEWS, gson.toJson(news)).commit()
    }

    override suspend fun insertNews(news: NewsPreference): NewsPreference {
        val actualNews = getAllNews().toMutableList()
        actualNews.add(news)

        editor.putString(NEWS, gson.toJson(actualNews)).commit()
        return news
    }

    override suspend fun getNewsByAuthorId(authorId: String): List<NewsPreference> {
        return getAllNews().filter { it.authorId == authorId }
    }


    private fun getAllNews(): List<NewsPreference> {
        val jsonNews = prefs.getString(NEWS, "")
        if (jsonNews == null || jsonNews.isEmpty()) return emptyList()

        return gson.fromJson(jsonNews, Array<NewsPreference>::class.java).toList()
    }

    override suspend fun updateNews(news: NewsPreference): NewsPreference {
        return getAllNews()
            .updateById(news)
            .also { editor.putString(NEWS, gson.toJson(it)).commit() }
            .let { news }
    }

    override suspend fun deleteNews(newsId: String) {
        val news = getAllNews().filter { it.id != newsId }
        editor.putString(NEWS, gson.toJson(news)).commit()
    }


    companion object {
        private const val AUTHORS = "authors"
        private const val NEWS = "news"
    }
}

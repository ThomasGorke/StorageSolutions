package com.thomasgorke.storagesolution.core.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.model.prefs.AuthorPreference
import com.thomasgorke.storagesolution.core.model.prefs.NewsPreference

interface SpStorage {
    suspend fun insertAuthor(newAuthor: AuthorPreference): AuthorPreference
    suspend fun getAllAuthors(): List<AuthorPreference>

    suspend fun insertNews(news: NewsPreference): NewsPreference
    suspend fun getNewsByAuthorId(authorId: Long): List<NewsPreference>
}

class SpStorageImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson
) : SpStorage {

    private val editor = prefs.edit()

    override suspend fun insertAuthor(newAuthor: AuthorPreference): AuthorPreference {
        val actualAuthors = getAllAuthors().toMutableList()
        actualAuthors.add(newAuthor.apply { id = getNextAuthorId() })

        editor.putString(AUTHORS, gson.toJson(actualAuthors)).commit()
        return newAuthor
    }

    override suspend fun getAllAuthors(): List<AuthorPreference> {
        val jsonAuthors = prefs.getString(AUTHORS, "")
        if (jsonAuthors == null || jsonAuthors.isEmpty()) return emptyList()

        return gson.fromJson(jsonAuthors, Array<AuthorPreference>::class.java).toList()
    }

    override suspend fun insertNews(news: NewsPreference): NewsPreference {
        val actualNews = getNewsByAuthorId(news.authorId).toMutableList()
        actualNews.add(news.apply { id = getNextNewsId() })

        editor.putString(NEWS, gson.toJson(actualNews)).commit()
        return news
    }

    override suspend fun getNewsByAuthorId(authorId: Long): List<NewsPreference> {
        return getAllNews().filter { it.authorId == authorId }
    }


    private fun getAllNews(): List<NewsPreference> {
        val jsonNews = prefs.getString(NEWS, "")
        if (jsonNews == null || jsonNews.isEmpty()) return emptyList()

        return gson.fromJson(jsonNews, Array<NewsPreference>::class.java)
            .toList()
    }


    private suspend fun getNextAuthorId(): Long {
        return try {
            getAllAuthors().last().id + 1
        } catch (e: NoSuchElementException) {
            0
        }
    }

    private fun getNextNewsId(): Long {
        return try {
            getAllNews().last().id + 1
        } catch (e: NoSuchElementException) {
            0
        }
    }

    companion object {
        private const val AUTHORS = "authors"
        private const val NEWS = "news"
    }
}

package com.thomasgorke.storagesolution.core.local.file

import android.content.Context
import com.google.gson.Gson
import com.thomasgorke.storagesolution.core.model.file.FileAuthor
import com.thomasgorke.storagesolution.core.model.file.FileNews
import com.thomasgorke.storagesolution.core.model.prefs.AuthorPreference
import com.thomasgorke.storagesolution.core.utils.updateById
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

interface FileStorage {
    suspend fun insertAuthor(newAuthor: FileAuthor): FileAuthor
    suspend fun getAllAuthors(): List<FileAuthor>
    suspend fun deleteAuthor(authorId: String)

    suspend fun insertNews(news: FileNews): FileNews
    suspend fun getNewsByAuthorId(authorId: String): List<FileNews>
    suspend fun updateNews(news: FileNews): FileNews
    suspend fun deleteNews(newsId: String)
}

class FileStorageImpl(
    private val context: Context,
    private val gson: Gson
) : FileStorage {

    private lateinit var authorsFile: File
    private lateinit var newsFile: File

    init {
        createDirectoryAndFiles()
    }

    private fun createDirectoryAndFiles() {
        try {
            val dir = File(context.filesDir, PATH).apply { mkdirs() }
            authorsFile = File(dir, AUTHORS_FILE).apply { createNewFile() }
            newsFile = File(dir, NEWS_FILE).apply { createNewFile() }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun getNewsFileContent(): List<FileNews> {
        val jsonNews = newsFile.readText()

        Timber.d(jsonNews)

        if (jsonNews.isEmpty()) return emptyList()
        return gson.fromJson(jsonNews, Array<FileNews>::class.java).toList()
    }

    private fun writeAuthorsToFile(data: String) {
        authorsFile.writeText(data)
    }

    private fun writeNewsToFile(data: String) {
        newsFile.writeText(data)
    }


    override suspend fun insertAuthor(newAuthor: FileAuthor): FileAuthor {
        val currentAuthors = getAllAuthors().toMutableList()
        currentAuthors.add(newAuthor)

        writeAuthorsToFile(gson.toJson(currentAuthors))
        return newAuthor
    }

    override suspend fun getAllAuthors(): List<FileAuthor> {
        val jsonAuthors = authorsFile.readText()

        Timber.d(jsonAuthors)

        if (jsonAuthors.isEmpty()) return emptyList()
        return gson.fromJson(jsonAuthors, Array<FileAuthor>::class.java).toList()
    }

    override suspend fun deleteAuthor(authorId: String) {
        //delete author
        val authors = getAllAuthors().filter { it.id != authorId }
        writeAuthorsToFile(gson.toJson(authors))

        //delete news
        val news = getNewsFileContent().filter { it.authorId != authorId }
        writeNewsToFile(gson.toJson(news))
    }

    override suspend fun insertNews(news: FileNews): FileNews {
        val currentNews = getNewsFileContent().toMutableList()
        currentNews.add(news)

        writeNewsToFile(gson.toJson(currentNews))
        return news
    }

    override suspend fun getNewsByAuthorId(authorId: String): List<FileNews> {
        return getNewsFileContent().filter { it.authorId == authorId }
    }

    override suspend fun updateNews(news: FileNews): FileNews {
        return getNewsFileContent()
            .updateById(news)
            .also { writeNewsToFile(gson.toJson(it)) }
            .let { news }
    }

    override suspend fun deleteNews(newsId: String) {
        val news = getNewsFileContent().filter { it.id != newsId }
        writeNewsToFile(gson.toJson(news))
    }


    companion object {
        private const val AUTHORS_FILE = "authors.txt"
        private const val NEWS_FILE = "news.txt"

        private const val PATH = "files"
    }
}

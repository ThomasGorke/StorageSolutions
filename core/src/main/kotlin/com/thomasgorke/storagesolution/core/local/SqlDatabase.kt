package com.thomasgorke.storagesolution.core.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.thomasgorke.storagesolution.core.local.SqlContract.AuthorEntity
import com.thomasgorke.storagesolution.core.local.SqlContract.NewsEntity
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.utils.toBitmap
import com.thomasgorke.storagesolution.core.utils.toByteArray
import java.sql.SQLData

private const val SQL_DB_NAME = "Storage_Solution_SQL_DB.db"


interface SqlDatabase {
    suspend fun insertAuthor(newAuthor: Author): Author
    suspend fun getAllAuthors(): List<Author>

    suspend fun insertNews(news: News): News
}


class SqlDatabaseImpl(
    context: Context
) : SQLiteOpenHelper(context, SQL_DB_NAME, null, 1), SqlDatabase {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.run {
            execSQL(SqlDao.createAuthorTable())
            execSQL(SqlDao.createNewsTable())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SqlDao.dropTable())
        onCreate(db)
    }


    override suspend fun insertAuthor(newAuthor: Author): Author =
        ContentValues().apply {
            put(AuthorEntity.NAME, newAuthor.name)
            put(AuthorEntity.IMAGE, newAuthor.image?.toByteArray())
        }.let {
            writableDatabase
                .insertWithOnConflict(
                    AuthorEntity.TABLE_NAME,
                    null,
                    it,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
                .let { newAuthor.apply { id = it } }
        }

    override suspend fun getAllAuthors(): List<Author> {
        val cursor = readableDatabase.query(
            AuthorEntity.TABLE_NAME,
            arrayOf(BaseColumns._ID, AuthorEntity.NAME, AuthorEntity.IMAGE),
            null,
            null,
            null,
            null,
            "${AuthorEntity.NAME} ASC"
        )

        val authors: MutableList<Author> = mutableListOf()

        with(cursor) {
            while (moveToNext()) {
                val authorId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(AuthorEntity.NAME))
                val imageBytes = getBlob(getColumnIndexOrThrow(AuthorEntity.IMAGE))

                authors.add(
                    Author(
                        name,
                        imageBytes.toBitmap(),
                        getNewsByAuthorId(authorId)
                    ).apply { id = authorId })
            }
        }

        return authors
    }

    override suspend fun insertNews(news: News): News =
        ContentValues().apply {
            put(NewsEntity.TITLE, news.headline)
            put(NewsEntity.CONTENT, news.content)
            put(NewsEntity.AUTHOR_ID, news.authorId)
        }.let {
            writableDatabase
                .insertWithOnConflict(
                    NewsEntity.TABLE_NAME,
                    null,
                    it,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
                .let { news.apply { id = it } }
        }

    private suspend fun getNewsByAuthorId(authorId: Long): List<News> {
        val cursor = readableDatabase.query(
            NewsEntity.TABLE_NAME,
            arrayOf(BaseColumns._ID, NewsEntity.TITLE, NewsEntity.CONTENT, NewsEntity.AUTHOR_ID),
            "${NewsEntity.AUTHOR_ID} = ?",
            arrayOf(authorId.toString()),
            null,
            null,
            "${NewsEntity.TITLE} ASC"
        )

        val news: MutableList<News> = mutableListOf()

        with(cursor) {
            while (moveToNext()) {
                val newsId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val title = getString(getColumnIndexOrThrow(NewsEntity.TITLE))
                val content = getString(getColumnIndexOrThrow(NewsEntity.CONTENT))

                news.add(News(title, content, authorId).apply { id = newsId })
            }
        }

        return news
    }
}


object SqlDao {

    fun dropTable(): String = "DROP TABLE IF EXISTS ${AuthorEntity.TABLE_NAME}"

    fun createAuthorTable(): String =
        "CREATE TABLE ${AuthorEntity.TABLE_NAME} (${BaseColumns._ID} INTEGER PRIMARY KEY, ${AuthorEntity.NAME} TEXT, ${AuthorEntity.IMAGE} BLOB);"

    fun createNewsTable(): String =
        "CREATE TABLE ${NewsEntity.TABLE_NAME} (${BaseColumns._ID} INTEGER PRIMARY KEY, ${NewsEntity.TITLE} TEXT, ${NewsEntity.CONTENT} TEXT,  ${NewsEntity.AUTHOR_ID} LONG);"
}

object SqlContract {
    object AuthorEntity : BaseColumns {
        const val TABLE_NAME = "t_authors"
        const val NAME = "name"
        const val IMAGE = "author_image"
    }

    object NewsEntity : BaseColumns {
        const val TABLE_NAME = "t_news"
        const val TITLE = "title"
        const val CONTENT = "content"
        const val AUTHOR_ID = "author_id"
    }
}

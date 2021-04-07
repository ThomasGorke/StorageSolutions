package com.thomasgorke.storagesolution.core.local.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.thomasgorke.storagesolution.core.local.sql.SqlTables.AuthorEntity
import com.thomasgorke.storagesolution.core.local.sql.SqlTables.NewsEntity
import com.thomasgorke.storagesolution.core.model.sql.SqlAuthorEntity
import com.thomasgorke.storagesolution.core.model.sql.SqlNewsEntity
import java.util.*

private const val SQL_DB_NAME = "Storage_Solution_SQL_DB.db"


interface SqlDatabase {
    suspend fun insertAuthor(newAuthor: SqlAuthorEntity): SqlAuthorEntity
    suspend fun getAllAuthors(): List<SqlAuthorEntity>

    suspend fun insertNews(news: SqlNewsEntity): SqlNewsEntity
    suspend fun getNewsByAuthorId(authorId: String): List<SqlNewsEntity>
}


class SqlDatabaseImpl(
    context: Context
) : SQLiteOpenHelper(
    context,
    SQL_DB_NAME, null, 1
),
    SqlDatabase {

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


    override suspend fun insertAuthor(newAuthor: SqlAuthorEntity): SqlAuthorEntity =
        ContentValues().apply {
            put(AuthorEntity.ID, UUID.randomUUID().toString())
            put(AuthorEntity.NAME, newAuthor.name)
            put(AuthorEntity.IMAGE, newAuthor.image)
        }.let {
            writableDatabase
                .insertWithOnConflict(
                    AuthorEntity.TABLE_NAME,
                    null,
                    it,
                    SQLiteDatabase.CONFLICT_REPLACE
                )

            newAuthor
        }

    override suspend fun getAllAuthors(): List<SqlAuthorEntity> {
        val cursor = readableDatabase.query(
            AuthorEntity.TABLE_NAME,
            arrayOf(AuthorEntity.ID, AuthorEntity.NAME, AuthorEntity.IMAGE),
            null,
            null,
            null,
            null,
            "${AuthorEntity.NAME} ASC"
        )

        val authors: MutableList<SqlAuthorEntity> = mutableListOf()

        with(cursor) {
            while (moveToNext()) {
                val authorId = getString(getColumnIndexOrThrow(AuthorEntity.ID))
                val name = getString(getColumnIndexOrThrow(AuthorEntity.NAME))
                val imageBytes = getBlob(getColumnIndexOrThrow(AuthorEntity.IMAGE))

                authors.add(SqlAuthorEntity(authorId, name, imageBytes))
            }
        }

        return authors
    }

    override suspend fun insertNews(news: SqlNewsEntity): SqlNewsEntity =
        ContentValues().apply {
            put(NewsEntity.ID, news.id)
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

            news
        }

    override suspend fun getNewsByAuthorId(authorId: String): List<SqlNewsEntity> {
        val cursor = readableDatabase.query(
            NewsEntity.TABLE_NAME,
            arrayOf(
                NewsEntity.ID,
                NewsEntity.TITLE,
                NewsEntity.CONTENT,
                NewsEntity.AUTHOR_ID
            ),
            "${NewsEntity.AUTHOR_ID} = ?",
            arrayOf(authorId.toString()),
            null,
            null,
            "${NewsEntity.TITLE} ASC"
        )

        val news: MutableList<SqlNewsEntity> = mutableListOf()

        with(cursor) {
            while (moveToNext()) {
                val newsId = getString(getColumnIndexOrThrow(AuthorEntity.ID))
                val title = getString(getColumnIndexOrThrow(NewsEntity.TITLE))
                val content = getString(getColumnIndexOrThrow(NewsEntity.CONTENT))

                news.add(SqlNewsEntity(newsId, title, content, authorId))
            }
        }

        return news
    }
}

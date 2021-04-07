package com.thomasgorke.storagesolution.core.local.sql

import android.provider.BaseColumns
import com.thomasgorke.storagesolution.core.local.sql.SqlTables.AuthorEntity
import com.thomasgorke.storagesolution.core.local.sql.SqlTables.NewsEntity

object SqlDao {

    fun dropTable(): String = "DROP TABLE IF EXISTS ${AuthorEntity.TABLE_NAME}"

    fun createAuthorTable(): String =
        "CREATE TABLE ${AuthorEntity.TABLE_NAME} (${AuthorEntity.ID} TEXT PRIMARY KEY, ${AuthorEntity.NAME} TEXT, ${AuthorEntity.IMAGE} BLOB);"

    fun createNewsTable(): String =
        "CREATE TABLE ${NewsEntity.TABLE_NAME} (${NewsEntity.ID} TEXT PRIMARY KEY, ${NewsEntity.TITLE} TEXT, ${NewsEntity.CONTENT} TEXT,  ${NewsEntity.AUTHOR_ID} LONG);"
}
package com.thomasgorke.storagesolution.core.local.sql

import android.provider.BaseColumns

object SqlTables {
    object AuthorEntity : BaseColumns {
        const val TABLE_NAME = "t_authors"
        const val NAME = "name"
        const val IMAGE = "author_image"
        const val ID = "id"
    }

    object NewsEntity : BaseColumns {
        const val TABLE_NAME = "t_news"
        const val TITLE = "title"
        const val CONTENT = "content"
        const val AUTHOR_ID = "author_id"
        const val ID = "id"
    }
}
package com.thomasgorke.storagesolution.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.model.prefs.AuthorPreference
import com.thomasgorke.storagesolution.core.model.prefs.NewsPreference
import com.thomasgorke.storagesolution.core.model.room.AuthorEntity
import com.thomasgorke.storagesolution.core.model.room.NewsEntity
import com.thomasgorke.storagesolution.core.model.sql.SqlAuthorEntity
import com.thomasgorke.storagesolution.core.model.sql.SqlNewsEntity
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(): ByteArray {
    return ByteArrayOutputStream().also {
        this.compress(Bitmap.CompressFormat.PNG, 100, it)
    }.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}


// Converter to Database models
fun Author.toPreference(): AuthorPreference = AuthorPreference(this.name, this.image.toByteArray())
fun News.toPreference(authorId: Long): NewsPreference =
    NewsPreference(this.headline, this.content, authorId)

fun Author.toSqlEntity(): SqlAuthorEntity = SqlAuthorEntity(this.name, this.image.toByteArray())
fun News.toSqlEntity(authorId: Long): SqlNewsEntity =
    SqlNewsEntity(this.headline, this.content, authorId)

fun Author.toRoomEntity(): AuthorEntity = AuthorEntity(this.name, this.image.toByteArray())
fun News.toRoomEntity(authorId: Long): NewsEntity =
    NewsEntity(this.headline, this.content, authorId)


// Converter from Database models
fun AuthorPreference.toAuthor(): Author = Author(this.name, this.image.toBitmap())
fun NewsPreference.toNews(): News = News(this.title, this.content)

fun SqlAuthorEntity.toAuthor(): Author = Author(this.name, this.image.toBitmap())
fun SqlNewsEntity.toNews(): News = News(this.headline, this.content)

fun AuthorEntity.toAuthor(): Author = Author(this.name, this.image.toBitmap())
fun NewsEntity.toNews(): News = News(this.title, this.content)
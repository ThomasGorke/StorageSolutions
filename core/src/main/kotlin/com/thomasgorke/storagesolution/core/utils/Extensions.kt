package com.thomasgorke.storagesolution.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.model.prefs.AuthorPreference
import com.thomasgorke.storagesolution.core.model.prefs.NewsPreference
import com.thomasgorke.storagesolution.core.model.room.RoomAuthorEntity
import com.thomasgorke.storagesolution.core.model.room.RoomNewsEntity
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
fun Author.toPreference(): AuthorPreference =
    AuthorPreference(this.id, this.name, this.image.toByteArray())

fun News.toPreference(authorId: String = ""): NewsPreference =
    NewsPreference(this.id, this.headline, this.content, authorId)

fun Author.toSqlEntity(): SqlAuthorEntity =
    SqlAuthorEntity(this.id, this.name, this.image.toByteArray())

fun News.toSqlEntity(authorId: String): SqlNewsEntity =
    SqlNewsEntity(this.id, this.headline, this.content, authorId)

fun Author.toRoomEntity(): RoomAuthorEntity =
    RoomAuthorEntity(this.id, this.name, this.image.toByteArray())

fun News.toRoomEntity(authorId: String): RoomNewsEntity =
    RoomNewsEntity(this.id, this.headline, this.content, authorId)


// Converter from Database models
fun AuthorPreference.toAuthor(): Author = Author(this.name, this.image.toBitmap(), this.id)
fun NewsPreference.toNews(): News = News(this.title, this.content, this.id)

fun SqlAuthorEntity.toAuthor(): Author = Author(this.name, this.image.toBitmap(), this.id)
fun SqlNewsEntity.toNews(): News = News(this.headline, this.content, this.id)

fun RoomAuthorEntity.toAuthor(): Author = Author(this.name, this.image.toBitmap(), this.id)
fun RoomNewsEntity.toNews(): News = News(this.title, this.content, this.id)

// helper
fun Iterable<NewsPreference>.updateById(updatedElement: NewsPreference): Iterable<NewsPreference> {
    for (element in this) {
        if (element.id == updatedElement.id) {
            element.apply {
                title = updatedElement.title
                content = updatedElement.content
            }
        }
    }
    return this
}
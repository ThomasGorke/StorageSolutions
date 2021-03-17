package com.thomasgorke.storagesolution.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.room.AuthorEntity
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
fun Author.toRoomEntity(): AuthorEntity = AuthorEntity(this.name, this.image.toByteArray())

fun Author.toSql



// Converter from Database models

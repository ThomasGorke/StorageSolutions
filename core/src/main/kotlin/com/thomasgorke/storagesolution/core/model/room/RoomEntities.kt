package com.thomasgorke.storagesolution.core.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "t_author")
data class AuthorEntity(
    val name: String,
    val image: ByteArray
) {
    @PrimaryKey
    var id: Long = 0
}

@Entity(tableName = "t_news")
data class NewsEntity(
    val title: String,
    val content: String,
    val authorId: Long
) {
    @PrimaryKey
    var id: Long = 0
}
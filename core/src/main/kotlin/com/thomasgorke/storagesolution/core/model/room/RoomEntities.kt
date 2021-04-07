package com.thomasgorke.storagesolution.core.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "t_author")
data class RoomAuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val image: ByteArray
)

@Entity(tableName = "t_news")
data class RoomNewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val authorId: String
)
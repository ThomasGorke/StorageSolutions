package com.thomasgorke.storagesolution.core.model.sql

data class SqlAuthorEntity(
    var id: String,
    val name: String,
    val image: ByteArray,
) {
}

data class SqlNewsEntity(
    var id: String,
    val headline: String,
    val content: String,
    val authorId: String,
)

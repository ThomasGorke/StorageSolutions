package com.thomasgorke.storagesolution.core.model.sql

data class SqlAuthorEntity(
    val name: String,
    val image: ByteArray
) {
    var id: Long = 0
}

data class SqlNewsEntity(
    val headline: String,
    val content: String,
    val authorId: Long
) {
    var id: Long = 0
}
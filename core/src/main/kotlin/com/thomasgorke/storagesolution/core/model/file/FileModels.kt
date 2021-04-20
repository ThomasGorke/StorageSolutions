package com.thomasgorke.storagesolution.core.model.file

data class FileAuthor(
    var id: String,
    val name: String,
    val image: ByteArray,
)

data class FileNews(
    var id: String,
    var title: String,
    var content: String,
    val authorId: String,
)
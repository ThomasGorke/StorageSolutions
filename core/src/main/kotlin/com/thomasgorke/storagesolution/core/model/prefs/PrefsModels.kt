package com.thomasgorke.storagesolution.core.model.prefs

data class AuthorPreference(
    var id: String,
    val name: String,
    val image: ByteArray,
)

data class NewsPreference(
    var id: String,
    var title: String,
    var content: String,
    val authorId: String,
)
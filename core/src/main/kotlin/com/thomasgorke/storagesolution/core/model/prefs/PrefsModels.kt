package com.thomasgorke.storagesolution.core.model.prefs

data class AuthorPreference(
    val name: String,
    val image: ByteArray
) {
    var id: Long = 0
}

data class NewsPreference(
    val title: String,
    val content: String,
    val authorId: Long
) {
    var id: Long = 0
}
package com.thomasgorke.storagesolution.core.model

data class News(
    val headline: String,
    val content: String
) {
    var id: Long = 0
}
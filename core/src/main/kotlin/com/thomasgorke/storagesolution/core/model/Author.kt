package com.thomasgorke.storagesolution.core.model

import android.graphics.Bitmap

data class Author(
    val name: String,
    val image: Bitmap,
    val news: List<News> = emptyList()
) {
    var id: Long = 0
}
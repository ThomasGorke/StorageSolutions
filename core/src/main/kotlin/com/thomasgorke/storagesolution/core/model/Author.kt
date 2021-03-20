package com.thomasgorke.storagesolution.core.model

import android.graphics.Bitmap

data class Author(
    val name: String,
    val image: Bitmap
) {
    var id: Long = 0
}
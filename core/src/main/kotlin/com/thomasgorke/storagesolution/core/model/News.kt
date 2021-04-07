package com.thomasgorke.storagesolution.core.model

import java.io.Serializable

data class News(
    val headline: String,
    val content: String,
    var id: String = "",
) : Serializable

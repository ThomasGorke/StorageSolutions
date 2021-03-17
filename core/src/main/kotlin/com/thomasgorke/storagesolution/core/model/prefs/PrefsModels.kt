package com.thomasgorke.storagesolution.core.model.prefs

import java.io.Serializable

data class AuthorPreference(
    val name: String,
    val image: String
) : Serializable

data class NewsPreference(
    val title: String,
    val content: String,
    val authorId: String
) : Serializable
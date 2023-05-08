package com.antsyferov.tfg.data.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Article(
    var id: String? = null,
    val author: String = "",
    val title: String = "",
    val description: String = "",
    val characterCount: Int = 0,
    @ServerTimestamp
    val createdAt: Date = Date()
)

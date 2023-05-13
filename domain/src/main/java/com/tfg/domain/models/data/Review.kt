package com.tfg.domain.models.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Review(
    var id: String = "",
    val articleId: String = "",
    val publicationId: String = "",
    val articleAuthorId: String = "",
    val reviewAuthorId: String = "",
    val rating: Int = 0,
    val description: String = "",
    val relevance: Int = 0,
    val comment: String = "",
    @ServerTimestamp
    val createdAt: Date = Date()
)

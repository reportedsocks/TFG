package com.tfg.domain.models.data

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Article(
    var id: String? = null,
    val author: String = "",
    val title: String = "",
    val description: String = "",
    val characterCount: Int = 0,
    @ServerTimestamp
    val createdAt: Date = Date(),
    @field:JvmField
    val isSelected: Boolean = false
)

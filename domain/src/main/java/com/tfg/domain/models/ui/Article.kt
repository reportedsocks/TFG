package com.tfg.domain.models.ui

import java.util.Date

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val characterCount: Int,
    val authorId: String,
    val createdAt: Date,
    var isSelected: Boolean
)

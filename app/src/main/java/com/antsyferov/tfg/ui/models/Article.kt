package com.antsyferov.tfg.ui.models

import java.util.Date

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val characterCount: Int,
    val authorId: String,
    val createdAt: Date
)

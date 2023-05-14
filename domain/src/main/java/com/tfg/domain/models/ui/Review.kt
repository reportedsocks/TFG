package com.tfg.domain.models.ui

import java.util.Date

data class Review(
    val id: String = "",
    val rating: Int,
    val description: String,
    val relevance: Int,
    val comment: String,
    val createdAt: Date = Date()
)

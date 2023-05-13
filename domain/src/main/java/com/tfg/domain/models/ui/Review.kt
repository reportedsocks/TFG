package com.tfg.domain.models.ui

data class Review(
    val rating: Int,
    val description: String,
    val relevance: Int,
    val comment: String
)

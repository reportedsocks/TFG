package com.tfg.domain.models.data

data class User(
    val articles: List<String> = emptyList(),
    val role: Int = 0
)

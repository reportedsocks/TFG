package com.antsyferov.tfg.data.models

data class User(
    val articles: List<String> = emptyList(),
    val role: Int = 0
)

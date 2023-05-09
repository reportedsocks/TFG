package com.tfg.domain.models.data

data class Publication(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val review_date: Long = 0L,
    val completion_date: Long = 0L
)

package com.tfg.domain.models.data

data class Customer(
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val role: Int = 0,
    val publicationId: String? = null,
    val articleId1: String? = null,
    val articleId2: String? = null,
    val articleId3: String? = null
)

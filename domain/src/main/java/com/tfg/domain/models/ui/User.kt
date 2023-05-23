package com.tfg.domain.models.ui

import android.net.Uri

data class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val avatar: Uri?,
    val role: UserRole = UserRole.AUTHOR,
    var publicationId: String? = null,
    val articleId1: String? = null,
    val articleId2: String? = null,
    val articleId3: String? = null
)

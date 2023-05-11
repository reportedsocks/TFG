package com.tfg.domain.models.ui

import android.net.Uri

data class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val avatar: Uri?,
    val role: UserRole = UserRole.AUTHOR
)

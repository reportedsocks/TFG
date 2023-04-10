package com.antsyferov.tfg.ui.models

import android.net.Uri

data class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val avatar: Uri?
)

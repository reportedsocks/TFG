package com.antsyferov.tfg.models

import androidx.annotation.DrawableRes
import java.util.Date

data class Publication(
    val id: Int,
    val title: String,
    val status: String,
    @DrawableRes
    val image: Int,
    val endDate: Date,
    val articlesCount: Int,
    val description: String
)

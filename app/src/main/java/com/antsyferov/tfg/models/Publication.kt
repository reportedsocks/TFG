package com.antsyferov.tfg.models

import androidx.annotation.DrawableRes
import com.antsyferov.tfg.R
import java.util.Date

data class Publication(
    val id: String,
    val title: String,
    val status: String = "Some Status",
    @DrawableRes
    val image: Int = R.drawable.publication1,
    val endDate: Date = Date(),
    val articlesCount: Int = 7,
    val description: String
)

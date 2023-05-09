package com.tfg.domain.models.ui

import androidx.annotation.DrawableRes
import com.tfg.domain.R

data class Publication(
    val id: String,
    val title: String,
    val description: String,
    val status: Status,
    @DrawableRes
    val image: Int = R.drawable.publication1
) {

    enum class Status {
        OPEN, IN_REVIEW, CLOSED
    }

}

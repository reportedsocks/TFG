package com.antsyferov.tfg.ui.models

import androidx.annotation.DrawableRes
import com.antsyferov.tfg.R
import java.util.Date

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

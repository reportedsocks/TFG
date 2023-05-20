package com.tfg.domain.models.ui

import androidx.annotation.DrawableRes
import com.tfg.domain.R
import java.util.Date

data class Publication(
    val id: String = "",
    val title: String,
    val description: String,
    val status: Status = Status.OPEN,
    val reviewDate: Date,
    val finalSubmitDate: Date,
    val completionDate: Date,
) {

    enum class Status {
        OPEN, IN_REVIEW, FINAL_SUBMIT, CLOSED
    }

}

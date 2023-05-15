package com.tfg.domain.models.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Publication(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    @ServerTimestamp
    val reviewDate: Date = Date(),
    @ServerTimestamp
    val finalSubmitDate: Date = Date(),
    @ServerTimestamp
    val completionDate: Date = Date()
)

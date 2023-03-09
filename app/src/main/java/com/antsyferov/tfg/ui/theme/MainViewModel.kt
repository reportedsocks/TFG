package com.antsyferov.tfg.ui.theme

import androidx.lifecycle.ViewModel
import com.antsyferov.tfg.R
import com.antsyferov.tfg.models.Publication
import java.util.*

class MainViewModel: ViewModel() {

    private val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"

    private val publications = listOf(
        Publication(id = 1, title = "Publication 1", status = "Active", image = R.drawable.publication1, endDate = Date(), articlesCount = 10, description),
        Publication(id = 2, title = "Publication 2", status = "Finished", image = R.drawable.publication1, endDate = Date(), articlesCount = 10, description),
        Publication(id = 3, title = "Publication 3", status = "Active", image = R.drawable.publication1, endDate = Date(), articlesCount = 10, description),
        Publication(id = 4, title = "Publication 4", status = "In Review", image = R.drawable.publication1, endDate = Date(), articlesCount = 10, description),
        Publication(id = 5, title = "Publication 5", status = "Active", image = R.drawable.publication1, endDate = Date(), articlesCount = 10, description),
        Publication(id = 6, title = "Publication 6", status = "Active", image = R.drawable.publication1, endDate = Date(), articlesCount = 10, description)
    )

    fun getPublications(): List<Publication> {
        return publications
    }

}
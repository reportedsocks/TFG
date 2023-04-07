package com.antsyferov.tfg.use_cases

import com.antsyferov.tfg.ui.models.Publication
import kotlinx.coroutines.flow.Flow

interface PublicationsListUseCase {

    fun getPublications(): Flow<List<Publication>>

}
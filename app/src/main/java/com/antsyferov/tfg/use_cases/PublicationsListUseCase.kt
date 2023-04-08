package com.antsyferov.tfg.use_cases

import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface PublicationsListUseCase {

    fun getPublications(): Flow<ResultOf<List<Publication>>>

}
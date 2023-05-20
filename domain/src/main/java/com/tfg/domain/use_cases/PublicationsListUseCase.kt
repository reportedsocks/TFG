package com.tfg.domain.use_cases

import com.tfg.domain.models.ui.Publication
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface PublicationsListUseCase {

    fun getPublications(): Flow<ResultOf<List<Publication>>>

    suspend fun getPublication(articleId: String, authorId: String): ResultOf<Publication>

    suspend fun addPublication(publication: Publication): ResultOf<Unit>

}
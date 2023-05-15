package com.tfg.domain.interfaces

import com.tfg.domain.models.data.Article
import com.tfg.domain.models.data.Publication
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface PublicationsDataSource {

    fun getPublications(): Flow<ResultOf<List<Publication>>>

    suspend fun getPublicationIdByArticle(articleId: String, authorId: String): ResultOf<String>

    suspend fun addPublication(publication: Publication): ResultOf<Unit>

}
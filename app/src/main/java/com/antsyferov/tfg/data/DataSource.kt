package com.antsyferov.tfg.data

import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Publication
import com.antsyferov.tfg.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface DataSource {

    fun getPublications(): Flow<ResultOf<List<Publication>>>

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>>

    suspend fun addArticle(publicationId: String, article: Article): ResultOf<Unit>

}
package com.antsyferov.tfg.data

import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Publication
import kotlinx.coroutines.flow.Flow

interface DataSource {

    fun getPublications(): Flow<List<Publication>>

    fun getArticles(publicationId: String): Flow<List<Article>>

    fun addArticle(publicationId: String, article: Article)

}
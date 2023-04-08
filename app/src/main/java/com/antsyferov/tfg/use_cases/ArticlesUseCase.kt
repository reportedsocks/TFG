package com.antsyferov.tfg.use_cases

import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.User
import kotlinx.coroutines.flow.Flow

interface ArticlesUseCase {

    fun getArticles(publicationId: String): Flow<List<Article>>

    fun addArticle(publicationId: String, title: String, user: User)

}
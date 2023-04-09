package com.antsyferov.tfg.use_cases

import android.net.Uri
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface ArticlesUseCase {

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>>

    suspend fun addArticle(publicationId: String, title: String, user: User, uri: Uri): ResultOf<Unit>

}